package com.careerconnect.exception;

import com.careerconnect.dto.common.ApiResp;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden
public class HandleException {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResp<?>> handlingRuntimeException(Exception e) {
        ApiResp<?> apiResponse = new ApiResp<>();
        apiResponse.setMessage(e.getMessage());
        apiResponse.setCode(500);
        e.printStackTrace();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResp<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResp<?> apiResponse = ApiResp.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    ResponseEntity<ApiResp<?>> handlingResourceNotFoundException(ResourceNotFoundException e) {
        ApiResp<?> apiResponse = ApiResp.builder()
                .code(25252)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }
    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        String errorMessage;
        int code;
        HttpStatus status = HttpStatus.FORBIDDEN;
        if (e instanceof BadCredentialsException) {
            errorMessage = "Sai tên đăng nhập hoặc mật khẩu.";
            code = 10000;
            status = HttpStatus.BAD_REQUEST;
        } else if (e instanceof LockedException) {
            errorMessage = "Tài khoản của bạn đã bị khóa.";
            code = 10001;
        } else {
            errorMessage = "Đã xảy ra lỗi không xác định";
            code = 99999;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ApiResp<?> response = ApiResp.builder()
                .code(code)
                .message(errorMessage)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResp<?>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        chỉ lấy message đầu tiên
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiResp<?> apiResponse = ApiResp.builder()
                .code(25252)
                .message(errorMessage)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

}
