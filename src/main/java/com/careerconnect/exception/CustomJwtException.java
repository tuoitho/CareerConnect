package com.careerconnect.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomJwtException extends AuthenticationException {
    public CustomJwtException(String message) {
        super(message);
    }
}
