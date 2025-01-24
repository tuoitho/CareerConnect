package com.careerconnect.mapper;

import com.careerconnect.dto.request.RegisterCompanyRequest;
import com.careerconnect.dto.response.CompanyResponse;
import com.careerconnect.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyResponse toCompanyResponse(Company company);

    @Mapping(target = "logo", ignore = true) // Bỏ qua trường logo
    Company toCompany(RegisterCompanyRequest registerCompanyRequest);

    @Mapping(target = "logo", ignore = true) // Bỏ qua trường logo
    void updateCompany(@MappingTarget Company company, RegisterCompanyRequest registerCompanyRequest);
}
