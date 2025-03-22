package com.careerconnect.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfileRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String fullname;
    @NotBlank(message = "Thông tin liên hệ không được để trống")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Số điện thoại phải đúng định dạng (ví dụ: +84123456789 hoặc 0123456789)")
    private String contact;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    @Size(max = 100, message = "Email không được dài quá 100 ký tự")
    private String email;
}
