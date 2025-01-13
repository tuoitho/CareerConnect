package com.careerconnect.exception;

import com.careerconnect.body.ApiResponse;
import com.careerconnect.util.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HandleException {

    //    AuthenticationException
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        //type of exception
        Logger.log("AuthenticationException: " + e.getMessage(), e.getClass().getName());
        String errorMessage;
        int code;
        if (e instanceof BadCredentialsException) {
            errorMessage = "Sai tên đăng nhập hoặc mật khẩu.";
            code = 500;
        } else if (e instanceof LockedException) {
            errorMessage = "Tài khoản của bạn đã bị khóa.";
            code = 403;
        } else {
            errorMessage = "Đã xảy ra lỗi không xác định";
            code = 500;
        }

        ApiResponse<?> response = ApiResponse.builder()
                .code(code)
                .message(errorMessage)
                .build();
        return ResponseEntity.status(code).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        ApiResponse<?> response = ApiResponse.builder()
                .code(500)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(500).body(response);
    }
}
