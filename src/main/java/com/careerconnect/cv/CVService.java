package com.careerconnect.cv;

import com.careerconnect.entity.CV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CVService {
    @Autowired
    private CVRepository cvRepository;
    
    public UserCV createCV(UserCV cv) {
        return cvRepository.save(cv);
    }
//    getAllCVs
    public List<UserCV> getAllCVs(Long userId) {
        return cvRepository.findByUserId(userId);
    }
    public UserCV updateCV(Long id, UserCV updatedCV) {
        UserCV existingCV = cvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CV not found"));
        
        existingCV.setName(updatedCV.getName());
        existingCV.setTemplateId(updatedCV.getTemplateId());
        existingCV.setContent(updatedCV.getContent());
        
        return cvRepository.save(existingCV);
    }
    
    public List<UserCV> getCVsByUserId(Long userId) {
        return cvRepository.findByUserId(userId);
    }
    
    public UserCV getCVById(Long id) {
        return cvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CV not found"));
    }
    
    @Transactional
    public void deleteCV( Long id) {
        cvRepository.deleteById(id);
    }
}