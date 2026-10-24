package com.vhub.smart.placement.dto;

public class LoginRequest {

    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Email
    private String email;
    @jakarta.validation.constraints.NotBlank
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    } 
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}