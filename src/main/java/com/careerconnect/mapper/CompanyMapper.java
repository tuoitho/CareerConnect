package com.careerconnect.mapper;

import com.careerconnect.dto.response.CompanyResponse;
import com.careerconnect.entity.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyResponse toCompanyResponse(Company company);
}
