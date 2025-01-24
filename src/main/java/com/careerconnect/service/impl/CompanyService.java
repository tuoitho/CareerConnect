package com.careerconnect.service.impl;

import com.careerconnect.dto.request.RegisterCompanyRequest;
import com.careerconnect.dto.response.CompanyResponse;
import com.careerconnect.entity.Company;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.mapper.CompanyMapper;
import com.careerconnect.repository.CompanyRepo;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepo companyRepo;
    private final AuthenticationHelper authenticationHelper;
    private final CompanyMapper companyMapper;
    private final RoleService roleService;
    private final UserService userService;
    private final UserRepository userRepository;
    public CompanyResponse getCurrentCompany(Long userId) {
        Recruiter recruiter= (Recruiter)userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }
        return companyMapper.toCompanyResponse(company);
    }

    public CompanyResponse registerCompany(Long userId, RegisterCompanyRequest registerCompanyRequest) {
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (recruiter.getCompany() != null) {
            throw new AppException(ErrorCode.COMPANY_ALREADY_REGISTERED);
        }
        Company company = companyMapper.toCompany(registerCompanyRequest);
        company.setRecruiters(List.of(recruiter));
        recruiter.setCompany(company);
        return companyMapper.toCompanyResponse(companyRepo.save(company));
    }


}
