package com.company.userservice.model.dto;

public class UserUpdate {
    private String firstName;
    private String lastName;
    private String phone;
    // any other partial-update fields you need

    // getters / setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
