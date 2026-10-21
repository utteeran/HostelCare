package com.example.hosteltracker.enums;

public enum ComplaintPriority {

    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String value;

    ComplaintPriority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}