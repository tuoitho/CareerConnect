package com.careerconnect.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCompanyRequest {
    private String name;
    private String address;
    private String website;
    private String description;
//    private String logo;
    private MultipartFile logo;
}

