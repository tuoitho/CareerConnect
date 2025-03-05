package com.careerconnect.service.impl;

import com.careerconnect.config.ChatMessageRepo;
import com.careerconnect.dto.response.UserChatResponse;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.ChatMessage;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepo chatMessageRepo;

    public List<UserChatResponse> getRecruitersForCandidate(Long candidateId) {
        Set<User> recruiters = chatMessageRepo.findUsersHavingMessageHistory(candidateId);

        List<UserChatResponse> recruiterDTOs = new ArrayList<>();
        for (User user : recruiters) {
            Recruiter recruiter = (Recruiter) user;
            UserChatResponse recruiterDTO = UserChatResponse.builder()
                            .id(recruiter.getUserId())
                        .name(recruiter.getUsername())
                        .avatar(recruiter.getCompany().getLogo())
                                .build();
            recruiterDTOs.add(recruiterDTO);
        }

        return recruiterDTOs;
    }

    public List<UserChatResponse> getCandidatesForRecruiter(Long recruiterId) {
        //
        Set<User> candidates = chatMessageRepo.findUsersHavingMessageHistory(recruiterId);
        List<UserChatResponse> candidateDTOs = new ArrayList<>();
        for (User user : candidates) {
            Candidate candidate = (Candidate) user;
            UserChatResponse candidateDTO = UserChatResponse.builder()
                    .id(candidate.getUserId())
                    .name(candidate.getUsername())
                    .avatar(candidate.getAvatar())
                    .build();
            candidateDTOs.add(candidateDTO);
        }
        return candidateDTOs;
    }
}
