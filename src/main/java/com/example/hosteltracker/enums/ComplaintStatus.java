package com.example.hosteltracker.enums;

public enum ComplaintStatus {

    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved");

    private final String value;

    ComplaintStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}