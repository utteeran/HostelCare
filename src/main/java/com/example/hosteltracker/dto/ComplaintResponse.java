package com.example.hosteltracker.dto;

public record ComplaintResponse(
        Long id,
        String studentName,
        String roomNumber,
        String category,
        String priority,
        String title,
        String description,
        String status,
        String createdAt,
        String verificationStatus,
        String verifiedBy,
        String assignedMechanic
) {
}