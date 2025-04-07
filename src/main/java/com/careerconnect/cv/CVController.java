package com.careerconnect.cv;

import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.entity.CV;
import com.careerconnect.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cvs")
public class CVController {
    @Autowired
    private CVService cvService;
    @Autowired
    private AuthenticationHelper authenticationHelper;
    @GetMapping
    public ResponseEntity<?> getAllCVs() {
        List<UserCV> cvs = cvService.getAllCVs(authenticationHelper.getUserId());
        ApiResp apiResp=ApiResp.builder()
                .result(cvs)
                .build();
        return ResponseEntity.ok(apiResp);
    }
    @PostMapping
    public ResponseEntity<?> createCV(@RequestBody UserCV cv) {
//        Long userId = authenticationHelper.getUserId();
//        cv.setUserId(userId);
//        UserCV createdCV = cvService.createCV(cv);
//        return ResponseEntity.ok(createdCV);
        Long userId = authenticationHelper.getUserId();
        cv.setUserId(userId);
        UserCV createdCV = cvService.createCV(cv);
        ApiResp apiResp=ApiResp.builder()
                .result(createdCV)
                .build();
        return ResponseEntity.ok(apiResp);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCV(@PathVariable Long id, @RequestBody UserCV cv) {
//        UserCV updatedCV = cvService.updateCV(id, cv);
//        return ResponseEntity.ok(updatedCV);
        Long userId = authenticationHelper.getUserId();
        cv.setUserId(userId);
        UserCV updatedCV = cvService.updateCV(id, cv);
        ApiResp apiResp=ApiResp.builder()
                .result(updatedCV)
                .build();
        return ResponseEntity.ok(apiResp);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCVById(@PathVariable Long id) {
//        UserCV cv = cvService.getCVById(id);
//        return ResponseEntity.ok(cv);
        UserCV cv = cvService.getCVById(id);
        ApiResp apiResp=ApiResp.builder()
                .result(cv)
                .build();
        return ResponseEntity.ok(apiResp);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCV(@PathVariable Long id) {
//        cvService.deleteCV(id);
//        return ResponseEntity.ok().build();
        Long userId = authenticationHelper.getUserId();
        cvService.deleteCV(id);
        ApiResp apiResp=ApiResp.builder()
                .code(200)
                .message("CV deleted successfully")
                .build();
        return ResponseEntity.ok(apiResp);
    }
}