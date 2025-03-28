package com.example.JWTProject.model;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {
    private String token;
    private String username;
    private boolean hasMembership;

    public JwtAuthenticationResponse(String token, String username, boolean hasMembership) {
        this.token = token;
        this.username = username;
        this.hasMembership = hasMembership;
    }
}
