package com.careerconnect.config;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.Invitation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterDTO {
    private Long id;
    private String avatar;
    private String fullname;
    private String contact;
    private String email;

}
@Data
@Builder @AllArgsConstructor
@NoArgsConstructor

class CandidateDTO {
    private Long id;
    private String avatar;
    private String fullname;
    private String contact;
    private String email;

}