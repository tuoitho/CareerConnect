package com.careerconnect.cv;

import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.cv.UserCVRequestDTO;
import com.careerconnect.dto.cv.UserCVResponseDTO;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cvs")
@RequiredArgsConstructor
public class UserCVController {
    private final UserCVService cvService;
    private final AuthenticationHelper authenticationHelper;
    
    @GetMapping
    public ResponseEntity<?> getAllCVs() {
        Long userId = authenticationHelper.getUserId();
        ApiResp<Object> apiResp = ApiResp.builder()
                .result(cvService.getAllCVs(userId))
                .build();
        return ResponseEntity.ok(apiResp);
    }
    
    @PostMapping
    public ResponseEntity<?> createCV(@RequestBody UserCVRequestDTO cvDTO) {
        Long userId = authenticationHelper.getUserId();
        ApiResp<Object> apiResp = ApiResp.builder()
                .result(cvService.createCV(cvDTO, userId))
                .build();
        return ResponseEntity.ok(apiResp);
    }
    @PreAuthorize("@userCVService.isOwner(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCV(@PathVariable Long id, @RequestBody UserCVRequestDTO cvDTO) {
        ApiResp<Object> apiResp = ApiResp.builder()
                .result(cvService.updateCV(id, cvDTO))
                .build();
        return ResponseEntity.ok(apiResp);
    }
    @PreAuthorize("@userCVService.isOwner(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCVById(@PathVariable Long id) {
        UserCVResponseDTO cv = cvService.getCVById(id);
        ApiResp<Object> apiResp = ApiResp.builder()
                .result(cv)
                .build();
        return ResponseEntity.ok(apiResp);
    }
    @PreAuthorize("@userCVService.isOwner(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCV(@PathVariable Long id) {
        cvService.deleteCV(id);
        ApiResp<Object> apiResp = ApiResp.builder()
                .message("CV deleted successfully")
                .build();
        return ResponseEntity.ok(apiResp);
    }
}