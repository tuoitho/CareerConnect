package com.careerconnect.mapper;

import com.careerconnect.dto.response.InvitationResponse;
import com.careerconnect.entity.Invitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface InvitationMapper {
    @Mappings({
            @Mapping(target = "inviterName", source = "inviter.fullname"),
            @Mapping(target = "companyName", source = "company.name")
    })
    InvitationResponse toInvitationResponse(Invitation invitation);
}
