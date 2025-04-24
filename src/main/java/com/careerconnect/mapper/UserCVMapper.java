package com.careerconnect.mapper;

import com.careerconnect.entity.UserCV;
import com.careerconnect.dto.request.UserCVRequestDTO;
import com.careerconnect.dto.response.UserCVResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserCVMapper {
    
    UserCVResponseDTO toDTO(UserCV entity);
    
    UserCV toEntity(UserCVRequestDTO dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    void updateEntityFromDTO(UserCVRequestDTO dto, @MappingTarget UserCV entity);
}