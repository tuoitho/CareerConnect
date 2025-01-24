package com.careerconnect.service.impl;

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


}
