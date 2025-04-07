package com.careerconnect.cv.service;

import com.careerconnect.cv.model.UserCV;
import com.careerconnect.cv.repository.CVRepository;
import com.careerconnect.entity.CV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CVService {

    @Autowired
    private CVRepository cvRepository;

    public UserCV createCV(UserCV cv) {
        return cvRepository.save(cv);
    }

    public List<UserCV> getAllCVs() {
        return cvRepository.findAll();
    }

    public UserCV getCVById(Long id) {
        return cvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CV not found with id: " + id));
    }

    public UserCV updateCV(Long id, UserCV cvDetails) {
        UserCV cv = getCVById(id);
        cv.setName(cvDetails.getName());
        cv.setContent(cvDetails.getContent());
        cv.setTemplateId(cvDetails.getTemplateId());
        return cvRepository.save(cv);
    }

    public void deleteCV(Long id) {
        UserCV cv = getCVById(id);
        cvRepository.delete(cv);
    }
}