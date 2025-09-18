package com.company.userservice.model.dto;

/**
 * Status DTO uses String for easier JSON requests; the service converts to User.Status
 */
public class UserUpdateStatus {
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
