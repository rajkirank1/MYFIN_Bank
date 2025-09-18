package com.company.userservice.model;

/**
 * Enum representing possible user statuses.
 */
public enum Status {
    ACTIVE,
    INACTIVE,
    PENDING,
    SUSPENDED,
    DELETED;

    public String toValue() {
        return this.name();
    }

    public static Status fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (Status s : values()) {
            if (s.name().equalsIgnoreCase(value.trim())) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown Status value: " + value);
    }
}
