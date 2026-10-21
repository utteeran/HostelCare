package com.example.hosteltracker.controller;

import com.example.hosteltracker.dto.AssignmentRequest;
import com.example.hosteltracker.dto.ComplaintRequest;
import com.example.hosteltracker.dto.ComplaintResponse;
import com.example.hosteltracker.dto.StatusRequest;
import com.example.hosteltracker.dto.VerificationRequest;
import com.example.hosteltracker.exception.ComplaintNotFoundException;
import com.example.hosteltracker.model.ComplaintEntity;
import com.example.hosteltracker.repository.ComplaintRepository;
import com.example.hosteltracker.validation.ComplaintValidator;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintRepository repository;

    public ComplaintController(ComplaintRepository repository) {
        this.repository = repository;
    }

    // Get all complaints
    @GetMapping
    public List<ComplaintResponse> getAllComplaints() {
        return repository.findAll()
                .stream()
                .map(this::toComplaintResponse)
                .sorted(Comparator.comparing(
                        ComplaintResponse::createdAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    // Test endpoint
    @GetMapping("/")
    public String home() {
        return "HostelCare is working!";
    }

    // Create a new complaint
    @PostMapping
    public ComplaintResponse createComplaint(
            @RequestBody ComplaintRequest request) {

        try {
            ComplaintValidator.validate(request);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage());
        }

        ComplaintEntity entity = new ComplaintEntity();

        entity.setStudentName(request.studentName().trim());
        entity.setRoomNumber(request.roomNumber().trim());
        entity.setCategory(request.category().trim());
        entity.setPriority(request.priority().trim());
        entity.setTitle(request.title().trim());
        entity.setDescription(request.description().trim());

        entity.setStatus("Pending");
        entity.setCreatedAt(Instant.now().toString());

        entity.setVerificationStatus("Pending");
        entity.setVerifiedBy(null);
        entity.setAssignedMechanic(null);

        ComplaintEntity saved = repository.save(entity);

        return toComplaintResponse(saved);
    }

    // Update complaint status
    @PatchMapping("/{id}/status")
    public ComplaintResponse updateStatus(
            @PathVariable long id,
            @RequestBody StatusRequest request) {

        ComplaintEntity entity = findComplaint(id);

        if (request.status() == null
                || !List.of(
                        "Pending",
                        "In Progress",
                        "Resolved")
                .contains(request.status())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status.");
        }

        entity.setStatus(request.status());

        ComplaintEntity saved = repository.save(entity);

        return toComplaintResponse(saved);
    }

    // Verify or reject a complaint
    @PatchMapping("/{id}/verification")
    public ComplaintResponse verifyComplaint(
            @PathVariable long id,
            @RequestBody VerificationRequest request) {

        ComplaintEntity entity = findComplaint(id);

        if (request.verificationStatus() == null
                || !List.of("Verified", "Rejected")
                .contains(request.verificationStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Verification status must be Verified or Rejected.");
        }

        if (request.verifiedBy() == null
                || request.verifiedBy().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "RC name is required.");
        }

        entity.setVerificationStatus(
                request.verificationStatus());

        entity.setVerifiedBy(request.verifiedBy().trim());

        ComplaintEntity saved = repository.save(entity);

        return toComplaintResponse(saved);
    }

    // Assign a mechanic
    @PatchMapping("/{id}/assign")
    public ComplaintResponse assignMechanic(
            @PathVariable long id,
            @RequestBody AssignmentRequest request) {

        ComplaintEntity entity = findComplaint(id);

        if (!"Verified".equalsIgnoreCase(
                entity.getVerificationStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only verified complaints can be assigned to a mechanic.");
        }

        if (request.assignedMechanic() == null
                || request.assignedMechanic().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Mechanic name is required.");
        }

        entity.setAssignedMechanic(
                request.assignedMechanic().trim());

        ComplaintEntity saved = repository.save(entity);

        return toComplaintResponse(saved);
    }

    // Find a complaint by ID
    private ComplaintEntity findComplaint(long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ComplaintNotFoundException(id));
    }

    // Automatically escalate overdue Medium complaints
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void escalateOverdueComplaints() {

        Instant now = Instant.now();

        List<ComplaintEntity> entities = repository.findAll();

        for (ComplaintEntity entity : entities) {

            boolean isMedium =
                    "Medium".equalsIgnoreCase(
                            entity.getPriority());

            boolean isUnresolved =
                    !"Resolved".equalsIgnoreCase(
                            entity.getStatus());

            if (entity.getCreatedAt() == null) {
                continue;
            }

            boolean fiveDaysPassed;

            try {
                fiveDaysPassed = Duration.between(
                        Instant.parse(entity.getCreatedAt()),
                        now
                ).compareTo(Duration.ofDays(5)) >= 0;

            } catch (Exception exception) {
                continue;
            }

            if (isMedium && isUnresolved && fiveDaysPassed) {
                entity.setPriority("High");
                repository.save(entity);
            }
        }
    }

    // Convert database entity to separate response DTO
    private ComplaintResponse toComplaintResponse(
            ComplaintEntity entity) {

        return new ComplaintResponse(
                entity.getId(),
                entity.getStudentName(),
                entity.getRoomNumber(),
                entity.getCategory(),
                entity.getPriority(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getVerificationStatus(),
                entity.getVerifiedBy(),
                entity.getAssignedMechanic()
        );
    }
}