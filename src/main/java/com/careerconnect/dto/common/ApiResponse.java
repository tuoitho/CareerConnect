package com.careerconnect.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T>{
    private Integer code;
    private String message;
    private T result;

}