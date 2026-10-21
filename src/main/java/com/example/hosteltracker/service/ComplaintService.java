package com.example.hosteltracker.service;

import com.example.hosteltracker.model.ComplaintEntity;
import com.example.hosteltracker.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public List<ComplaintEntity> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public ComplaintEntity getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found: " + id));
    }

    public ComplaintEntity saveComplaint(ComplaintEntity complaint) {
        return complaintRepository.save(complaint);
    }

    public void deleteComplaint(Long id) {
        ComplaintEntity complaint = getComplaintById(id);
        complaintRepository.delete(complaint);
    }
}