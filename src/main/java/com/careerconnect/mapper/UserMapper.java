package com.careerconnect.mapper;


import com.careerconnect.dto.request.RegisterRequest;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.entity.Role;
import com.careerconnect.entity.User;
import com.careerconnect.enums.UserType;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.repository.RoleRepository;
import com.careerconnect.service.impl.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    Recruiter mapToRecruiter(RegisterRequest registerRequest);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    Candidate mapToCandidate(RegisterRequest registerRequest);

    default User mapToUserBasedOnType(RegisterRequest registerRequest) {
        //convert to emun and catch exception if not found
        UserType usertype = null;
        try {
            usertype = UserType.valueOf(registerRequest.getUserType());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_USERTYPE);
        }
        return switch (usertype) {
            case RECRUITER -> mapToRecruiter(registerRequest);
            //TODO: add other user types
            case CANDIDATE -> mapToCandidate(registerRequest);
            default -> null;
        };
    }
}
