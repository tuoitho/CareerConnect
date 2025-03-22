package com.careerconnect.service.impl;

import com.careerconnect.dto.request.RegisterRequest;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.entity.Role;
import com.careerconnect.entity.User;
import com.careerconnect.enums.RoleEnum;
import com.careerconnect.mapper.UserMapper;
import com.careerconnect.repository.RoleRepository;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public String generateAccessToken(Authentication authentication) {
        return tokenProvider.generateAccessToken(authentication);
    }
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        User user = userMapper.mapToUserBasedOnType(registerRequest);
        if (user instanceof Candidate) {
            Role role = roleRepository.findByRoleName(RoleEnum.CANDIDATE)
                    .orElseGet(() -> roleRepository.save(Role.builder().roleName(RoleEnum.CANDIDATE).build()));
            user.setRole(role);
        } else if (user instanceof Recruiter) {
            Role role = roleRepository.findByRoleName(RoleEnum.RECRUITER)
                    .orElseGet(() -> roleRepository.save(Role.builder().roleName(RoleEnum.RECRUITER).build()));
            user.setRole(role);
        }
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        //password is hashed in the mapper
        userRepository.save(user);
    }
}
