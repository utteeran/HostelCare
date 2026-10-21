package com.example.hosteltracker.dto;

public record ComplaintRequest(
        String studentName,
        String roomNumber,
        String category,
        String priority,
        String title,
        String description
) {
}