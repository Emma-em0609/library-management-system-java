package com.example.library.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class RegisterRequest {

    @Schema(example = "newuser")
    private String username;

    @Schema(example = "password123")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}