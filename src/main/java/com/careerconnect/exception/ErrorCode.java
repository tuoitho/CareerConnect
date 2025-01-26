package com.careerconnect.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USERNAME_EXISTED(400, "Username is already taken", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(400, "Email is already taken", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(401, "You need to log in to perform this action.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),
    EXPIRED_TOKEN(401, "EXPIRED_TOKEN", HttpStatus.UNAUTHORIZED),
    INVALID_USERTYPE(400, "Invalid user type", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(404, "Role not found", HttpStatus.NOT_FOUND), COMPANY_NOT_FOUND(404, "Company not found", HttpStatus.NOT_FOUND),


    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),


    COMPANY_ALREADY_REGISTERED(400, "Company already registered", HttpStatus.BAD_REQUEST),
    INVITATION_NOT_FOUND(404, "Invitation not found", HttpStatus.NOT_FOUND),
    INVITATION_EXPIRED(400, "Invitation expired", HttpStatus.BAD_REQUEST),
    INVALID_INVITATION(400, "Invalid invitation", HttpStatus.BAD_REQUEST);











    ;
    ;
    ;
    ;


    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}