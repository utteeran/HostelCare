package com.example.hosteltracker.enums;

public enum VerificationStatus {

    PENDING("Pending"),
    VERIFIED("Verified"),
    REJECTED("Rejected");

    private final String value;

    VerificationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}