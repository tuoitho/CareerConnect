package com.careerconnect.dto.response;

import com.careerconnect.entity.Recruiter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponse {
    private Long companyId;
    private String name;
    private String address; // Địa chỉ
    private String website;
    private String description; // Mô tả
    private String logo;
    private Boolean active;
}
