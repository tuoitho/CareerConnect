package com.careerconnect.service;

import com.careerconnect.repository.UserCVRepository;
import com.careerconnect.dto.request.UserCVRequestDTO;
import com.careerconnect.dto.response.UserCVResponseDTO;
import com.careerconnect.mapper.UserCVMapper;
import com.careerconnect.entity.UserCV;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCVService {
    private final UserCVRepository userCvRepository;
    
    private final UserCVMapper userCVMapper;
    private final AuthenticationHelper authenticationHelper;

    public UserCVResponseDTO createCV(UserCVRequestDTO cvDTO, Long userId) {
        UserCV cv = userCVMapper.toEntity(cvDTO);
        cv.setUserId(userId);
        UserCV savedCV = userCvRepository.save(cv);
        return userCVMapper.toDTO(savedCV);
    }

    public List<UserCVResponseDTO> getAllCVs(Long userId) {
        List<UserCV> cvs = userCvRepository.findByUserId(userId);
        return cvs.stream()
                .map(userCVMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public UserCVResponseDTO updateCV(Long id, UserCVRequestDTO updatedCVDTO) {
        UserCV existingCV = userCvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CV not found"));
        
        userCVMapper.updateEntityFromDTO(updatedCVDTO, existingCV);
        
        UserCV savedCV = userCvRepository.save(existingCV);
        return userCVMapper.toDTO(savedCV);
    }
    
    public List<UserCVResponseDTO> getCVsByUserId(Long userId) {
        List<UserCV> cvs = userCvRepository.findByUserId(userId);
        return cvs.stream()
                .map(userCVMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public UserCVResponseDTO getCVById(Long id) {
        UserCV cv = userCvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CV not found"));
        return userCVMapper.toDTO(cv);
    }
    
    @Transactional
    public void deleteCV(Long id) {
        userCvRepository.deleteById(id);
    }

    public boolean isOwner(Long id) {
        if (id == null) {
            return false;
        }

        Long currentUserId = authenticationHelper.getUserId();
        if (currentUserId == null) {
            return false;
        }

        return userCvRepository.findById(id)
                .map(cv -> cv.getUserId().equals(currentUserId))
                .orElse(false);
    }
}