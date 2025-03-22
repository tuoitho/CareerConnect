package com.careerconnect.controller;

import com.careerconnect.constant.ApiEndpoint;
import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.dto.request.AddMemberRequest;
import com.careerconnect.service.ImageService;
import com.careerconnect.service.impl.CompanyService;
import com.careerconnect.util.AuthenticationHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiEndpoint.PREFIX+"/invitation")
@PreAuthorize(SecurityEndpoint.RECRUITER)
public class InvitationController {
    private final CompanyService companyService;
    private final AuthenticationHelper authenticationHelper;
    private final ImageService imageService;
    
    @GetMapping
    public ResponseEntity<?> getInvitations(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "2") int size) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Lấy thông tin thành công")
                .result(companyService.getInvitations(authenticationHelper.getUserId(),page,size))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteMember(@Valid @RequestBody AddMemberRequest addMemberRequest) {
        companyService.addMember(authenticationHelper.getUserId(),addMemberRequest);
        ApiResponse<?> response = ApiResponse.builder()
                .message("Mời thành viên thành công")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinCompany(@RequestParam String token) {
        Long userId = authenticationHelper.getUserId();
        ApiResponse<?> response = ApiResponse.builder()
                .message("Tham gia công ty thành công")
                .result(companyService.accept(userId,token))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getInvitationByToken(@PathVariable String token) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Lấy thông tin thành công")
                .result(companyService.getInvitation(token))
                .build();
        return ResponseEntity.ok(response);
    }


}
