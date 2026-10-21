package com.example.hosteltracker.dto;

public record VerificationRequest(
        String verificationStatus,
        String verifiedBy
) {
}