package com.instagram.User.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource) {
        super(String.format("Resource %s not found", resource));
    }
}
