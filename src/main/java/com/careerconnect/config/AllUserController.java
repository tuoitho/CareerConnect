package com.careerconnect.config;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.repository.RecruiterRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoint.PREFIX)
@RequiredArgsConstructor
public class AllUserController {
    private final RecruiterRepo recruiterRepo;

    @GetMapping("/user/recruiters")
    public List<RecruiterDTO> getAllRecruiter() {
        // Lấy danh sách tất cả recruiter
        return recruiterRepo.findAll().stream().map((recruiter) -> {
            RecruiterDTO dto = new RecruiterDTO();
            dto.setId(recruiter.getUserId());
            dto.setAvatar("aaaaaaaaaa");
            dto.setFullname(recruiter.getFullname());
            dto.setContact(recruiter.getContact());
            dto.setEmail(recruiter.getEmail());
            return dto;
        }).toList();
    }
}
