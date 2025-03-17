package com.careerconnect.service.impl;

import com.careerconnect.repository.ChatMessageRepo;
import com.careerconnect.config.WebSocketEventListener;
import com.careerconnect.dto.response.UserChatResponse;
import com.careerconnect.entity.Candidate;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepo chatMessageRepo;
    private final WebSocketEventListener webSocketEventListener;

    public List<UserChatResponse> getRecruitersForCandidate(Long candidateId) {
        Set<User> recruiters = chatMessageRepo.findUsersHavingMessageHistory(candidateId);

        List<UserChatResponse> recruiterDTOs = new ArrayList<>();
        for (User user : recruiters) {
            Recruiter recruiter = (Recruiter) user;
            UserChatResponse recruiterDTO = UserChatResponse.builder()
                            .id(recruiter.getUserId())
                    .fullname(recruiter.getFullname())
                        .name(recruiter.getUsername())
                        .avatar(recruiter.getCompany().getLogo())
                    .active(webSocketEventListener.isUserOnline(recruiter.getUserId())) // Thêm trạng thái active
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
                    .fullname(candidate.getFullname())
                    .avatar(candidate.getAvatar())
                    .active(webSocketEventListener.isUserOnline(candidate.getUserId())) // Thêm trạng thái active
                    .build();
            candidateDTOs.add(candidateDTO);
        }
        return candidateDTOs;
    }
}
