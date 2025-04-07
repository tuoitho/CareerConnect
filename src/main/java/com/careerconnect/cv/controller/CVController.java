package com.careerconnect.cv.controller;

import com.careerconnect.cv.model.UserCV;
import com.careerconnect.cv.model.UserCV;
import com.careerconnect.cv.service.CVService;
import com.careerconnect.dto.common.ApiResp;
import com.cloudinary.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cvs")
@RequiredArgsConstructor
public class CVController {
    
    private final CVService cvService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createCV(@RequestBody UserCV cv) {
        ApiResp apiResp = ApiResp.builder()
                .message("CV created successfully")
                .result(cvService.createCV(cv))
                .build();
        return ResponseEntity.ok(apiResp);
    }

    @GetMapping
    public ResponseEntity<?> getAllCVs() {
//        return cvService.getAllCVs();
        ApiResp apiResp = ApiResp.builder()
                .message("CVs retrieved successfully")
                .result(cvService.getAllCVs())
                .build();
        return ResponseEntity.ok(apiResp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCVById(@PathVariable Long id) {
//        return cvService.getCVById(id);
        ApiResp apiResp = ApiResp.builder()
                .message("CV retrieved successfully")
                .result(cvService.getCVById(id))
                .build();
    return ResponseEntity.ok(apiResp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCV(@PathVariable Long id, @RequestBody UserCV cvDetails) {
//        return cvService.updateCV(id, cvDetails);
        ApiResp apiResp = ApiResp.builder()
                .message("CV updated successfully")
                .result(cvService.updateCV(id, cvDetails))
                .build();
        return ResponseEntity.ok(apiResp);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCV(@PathVariable Long id) {
        cvService.deleteCV(id);
        ApiResp apiResp = ApiResp.builder()
                .message("CV deleted successfully")
                .result(null)
                .build();
        return ResponseEntity.ok(apiResp);
    }
    //default CV
}