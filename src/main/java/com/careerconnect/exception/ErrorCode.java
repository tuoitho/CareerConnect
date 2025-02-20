package com.careerconnect.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USERNAME_EXISTED( "Username is already taken", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED( "Email is already taken", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(10003, "You need to log in to perform this action.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(10004, "You do not have permission", HttpStatus.FORBIDDEN),
    EXPIRED_TOKEN(10005, "EXPIRED_TOKEN", HttpStatus.UNAUTHORIZED),
    INVALID_USERTYPE(10006, "Invalid user type", HttpStatus.BAD_REQUEST),
    NO_LINKED_COMPANY(20021, "No company linked", HttpStatus.NOT_FOUND),
    COMPANY_ALREADY_REGISTERED(32452, "Company already registered", HttpStatus.BAD_REQUEST),
    INVITATION_EXPIRED(25356, "Invitation expired", HttpStatus.BAD_REQUEST),
    INVALID_INVITATION(15256, "Invalid invitation", HttpStatus.BAD_REQUEST),
    NOT_IN_COMPANY(34363, "You are not in a company", HttpStatus.BAD_REQUEST),
    NOT_PERMITTED(67252, "You are not permitted to perform this action", HttpStatus.FORBIDDEN),
    RESOURCE_MUST_BE_CREATED_OR_MODIFIED(75225, "Resource must be created or modify, plz don't provide id or id must be existed", HttpStatus.BAD_REQUEST),
    ALREADY_APPLIED(42452, "You have already applied for this job", HttpStatus.BAD_REQUEST),
    ROBOT_DETECTED( 85252, "You are a robot", HttpStatus.BAD_REQUEST),

            ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    ErrorCode(String message, HttpStatusCode statusCode) {
        this.code = null;
        this.message = message;
        this.statusCode = statusCode;
    }
    private final Integer code;
    private final String message;
    private final HttpStatusCode statusCode;

}