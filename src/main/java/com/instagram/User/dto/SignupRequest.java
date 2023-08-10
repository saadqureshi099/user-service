package com.instagram.User.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String username;
    private String profileUrl;
    private String password;
    private Boolean isPrivate;
}
