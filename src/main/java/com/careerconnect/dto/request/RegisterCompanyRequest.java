package com.careerconnect.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCompanyRequest {
    @NotBlank(message = "Tên công ty không được để trống")
    @Size(min = 2, max = 100, message = "Tên công ty phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 5, max = 200, message = "Địa chỉ phải từ 5 đến 200 ký tự")
    private String address;

    @NotBlank(message = "Website không được để trống")
    @URL(message = "Website phải là URL hợp lệ")
    @Size(max = 100, message = "Website không được dài quá 100 ký tự")
    private String website;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 20, max = 5000, message = "Mô tả phải từ 20 đến 5000 ký tự")
    private String description;

    @NotNull(message = "Logo không được để trống")
    private MultipartFile logo;
}

