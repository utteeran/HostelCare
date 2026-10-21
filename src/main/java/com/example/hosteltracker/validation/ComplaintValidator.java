package com.example.hosteltracker.validation;

import com.example.hosteltracker.dto.ComplaintRequest;

public class ComplaintValidator {

    public static void validate(ComplaintRequest request) {

        if (request.studentName() == null ||
                request.studentName().isBlank()) {
            throw new IllegalArgumentException(
                    "Student name is required");
        }

        if (request.roomNumber() == null ||
                request.roomNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Room number is required");
        }

        if (request.category() == null ||
                request.category().isBlank()) {
            throw new IllegalArgumentException(
                    "Complaint category is required");
        }

        if (request.title() == null ||
                request.title().isBlank()) {
            throw new IllegalArgumentException(
                    "Complaint title is required");
        }

        if (request.description() == null ||
                request.description().isBlank()) {
            throw new IllegalArgumentException(
                    "Complaint description is required");
        }

        if (request.priority() == null ||
                !request.priority().matches(
                        "Low|Medium|High")) {
            throw new IllegalArgumentException(
                    "Priority must be Low, Medium, or High");
        }
    }
}