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
public class RegisterRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
            message = "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới")
    private String username;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 100, message = "Mật khẩu phải từ 8 đến 100 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Mật khẩu phải chứa ít nhất một chữ cái thường, một chữ cái in hoa và một số")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    @Size(max = 100, message = "Email không được dài quá 100 ký tự")
    private String email;
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String fullname;

    @NotBlank(message = "Loại người dùng không được để trống")
    @Pattern(regexp = "^(CANDIDATE|RECRUITER|ADMIN)$",
            message = "Loại người dùng phải là CANDIDATE, RECRUITER hoặc ADMIN")
    private String userType;


}
