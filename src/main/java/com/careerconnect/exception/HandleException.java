package com.careerconnect.exception;

import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.util.Logger;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden
public class HandleException {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(Exception e) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(e.getMessage());
        apiResponse.setCode(400);
        e.printStackTrace();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    ResponseEntity<ApiResponse<?>> handlingResourceNotFoundException(ResourceNotFoundException e) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(404)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }
    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
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

}
