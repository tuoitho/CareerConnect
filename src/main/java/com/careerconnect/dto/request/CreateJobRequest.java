package com.careerconnect.dto.request;

import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateJobRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 5, max = 100, message = "Tiêu đề phải từ 5 đến 100 ký tự")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 20, max = 5000, message = "Mô tả phải từ 20 đến 5000 ký tự")
    private String description;

    @NotBlank(message = "Địa điểm không được để trống")
    @Size(max = 200, message = "Địa điểm không được dài quá 200 ký tự")
    private String location;

    @NotBlank(message = "Loại công việc không được để trống")
    @Pattern(regexp = "^(FULL_TIME|PART_TIME|CONTRACT|INTERNSHIP)$",
            message = "Loại công việc phải là FULL_TIME, PART_TIME, CONTRACT hoặc INTERNSHIP")
    private String type;

    @Pattern(regexp = "^\\d+$", message = "Mức lương tối thiểu phải là số")
    private String minSalary;

    @Pattern(regexp = "^\\d+$", message = "Mức lương tối đa phải là số")
    private String maxSalary;

    @NotNull(message = "Hạn chót không được để trống")
    @FutureOrPresent(message = "Hạn chót phải là hiện tại hoặc trong tương lai")
    private LocalDateTime deadline;

    @NotNull(message = "Kinh nghiệm không được để trống")
    private ExpEnum experience;

    @NotBlank(message = "Danh mục không được để trống")
    @Size(max = 50, message = "Danh mục không được dài quá 50 ký tự")
    private String category;

    private boolean active;
}