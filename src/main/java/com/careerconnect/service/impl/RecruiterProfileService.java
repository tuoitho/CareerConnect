package com.careerconnect.service.impl;

import com.careerconnect.dto.request.RecruiterProfileRequest;
import com.careerconnect.dto.response.RecruiterProfileResponse;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.RecruiterRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruiterProfileService {

    private final RecruiterRepo recruiterRepo;
    private final PasswordEncoder passwordEncoder;

    //  Cập nhật hồ sơ của recruiter
    @Transactional
    public RecruiterProfileResponse updateProfile(Long userId,RecruiterProfileRequest req) {
        Recruiter recruiter = recruiterRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        recruiter.setPassword(passwordEncoder.encode(req.getPassword()));
        recruiter.setFullname(req.getFullname());
        recruiter.setContact(req.getContact());
        recruiter.setEmail(req.getEmail());
        Recruiter savedRecruiter = recruiterRepo.save(recruiter);
        return RecruiterProfileResponse.builder()
                .username(savedRecruiter.getUsername())
                .password(savedRecruiter.getPassword())
                .fullname(savedRecruiter.getFullname())
                .contact(savedRecruiter.getContact())
                .email(savedRecruiter.getEmail())
                .build();
    }

    // Lấy thông tin hồ sơ của recruiter
    public RecruiterProfileResponse getProfile(Long userId) {
        Recruiter recruiter = recruiterRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        return RecruiterProfileResponse.builder()
                .username(recruiter.getUsername())
                .password(recruiter.getPassword())
                .fullname(recruiter.getFullname())
                .contact(recruiter.getContact())
                .email(recruiter.getEmail())
                .build();
    }

}