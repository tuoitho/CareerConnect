package com.careerconnect.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyJobRequest {
    @NotNull(message = "ID công việc không được để trống")
    private Long jobId;

    @Size(max = 5000, message = "Thư ứng tuyển không được dài quá 5000 ký tự")
    @Length(min = 10, message = "Thư ứng tuyển phải dài ít nhất 10 ký tự")
    private String coverLetter;

    @NotNull(message = "ID CV không được để trống")
    private Long cvId;
}
