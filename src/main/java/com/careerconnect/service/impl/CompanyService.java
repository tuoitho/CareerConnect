package com.careerconnect.service.impl;

import com.careerconnect.dto.common.MailDTO;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.request.AddMemberRequest;
import com.careerconnect.dto.request.RegisterCompanyRequest;
import com.careerconnect.dto.response.AllInvitationResponse;
import com.careerconnect.dto.response.CompanyResponse;
import com.careerconnect.dto.response.InvitationResponse;
import com.careerconnect.dto.response.MemberResponse;
import com.careerconnect.entity.Company;
import com.careerconnect.entity.Invitation;
import com.careerconnect.entity.Recruiter;
import com.careerconnect.exception.AppException;
import com.careerconnect.exception.ErrorCode;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.mapper.CompanyMapper;
import com.careerconnect.mapper.InvitationMapper;
import com.careerconnect.repository.CompanyRepo;
import com.careerconnect.repository.InvitationRepository;
import com.careerconnect.repository.RecruiterRepo;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.service.ImageService;
import com.careerconnect.service.MailService;
import com.careerconnect.service.PaginationService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final PaginationService paginationService;
    private final CompanyRepo companyRepo;
    private final CompanyMapper companyMapper;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final RedisTemplate<String, String> redisTemplate;
    private final InvitationRepository invitationRepository;
    private final MailService mailService;
    private final InvitationMapper invitationMapper;
    private final RecruiterRepo recruiterRepo;

    public CompanyResponse getCurrentCompany(Long userId) {
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NO_LINKED_COMPANY);
        }
        return companyMapper.toCompanyResponse(company);
    }

    @Transactional
    public CompanyResponse registerCompany(Long userId, RegisterCompanyRequest registerCompanyRequest) {
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        if (recruiter.getCompany() != null) {
            throw new AppException(ErrorCode.COMPANY_ALREADY_REGISTERED);
        }
        Company company = companyMapper.toCompany(registerCompanyRequest);
        company.setRecruiters(List.of(recruiter));
        if (registerCompanyRequest.getLogo() != null)
            company.setLogo(imageService.uploadCloudinary(registerCompanyRequest.getLogo()));
        recruiter.setCompany(company);
        recruiter.setRepresentative(true);
        userRepository.save(recruiter);
        return companyMapper.toCompanyResponse(companyRepo.save(company));
    }


    public CompanyResponse updateCompany(Long userId, RegisterCompanyRequest registerCompanyRequest) {
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NO_LINKED_COMPANY);
        }
        companyMapper.updateCompany(company, registerCompanyRequest);
        if (registerCompanyRequest.getLogo() != null)
            company.setLogo(imageService.uploadCloudinary(registerCompanyRequest.getLogo()));
        Logger.log(registerCompanyRequest);
        return companyMapper.toCompanyResponse(companyRepo.save(company));
    }
    @Transactional
    public void addMember(Long userId, AddMemberRequest addMemberRequest) {
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NO_LINKED_COMPANY);
        }
        //create a link to invite member
        String token = UUID.randomUUID().toString();
        Invitation invitation = Invitation.builder()
                .token(token)
                .email(addMemberRequest.getEmail())
                .company(company)
                .inviter(recruiter)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        invitationRepository.save(invitation);
        //send mail
        MailDTO mailDTO = MailDTO.builder()
                .from("no-reply@example.com")
                .text("You have been invited to join " + company.getName() + " on CareerConnect. Click the link below to accept the invitation. \n" +
                        "http://localhost:3000/recruiter/invitation/" + token)
                .to(addMemberRequest.getEmail())
                .subject("Invitation to join " + company.getName())
                .build();
        mailService.send(mailDTO);
    }
    @Transactional
    public InvitationResponse accept(Long userId, String token) {
        Invitation invitation = invitationRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException(RoleService.class, "token", token));
        if (!Objects.equals(userId, invitation.getInviter().getUserId())) {
            throw new AppException(ErrorCode.INVALID_INVITATION);
        }
        if (invitation.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVITATION_EXPIRED);
        }
        Company company = invitation.getCompany();
        Recruiter recruiter = (Recruiter) userRepository.findById(invitation.getInviter().getUserId()).orElseThrow(() -> new ResourceNotFoundException(RoleService.class, invitation.getInviter().getUserId()));
        recruiter.setCompany(company);
        userRepository.save(recruiter);
        invitation.setAccepted(true);
        invitation.setInvitee(recruiter);
        return invitationMapper.toInvitationResponse(invitationRepository.save(invitation));
    }

    public InvitationResponse getInvitation(String token) {
        Invitation invitation = invitationRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException(RoleService.class, "token", token));
        return invitationMapper.toInvitationResponse(invitation);
    }


    public PaginatedResponse<MemberResponse> getMembers(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NOT_IN_COMPANY);
        }
        Page<Recruiter> recruiters = recruiterRepo.findAllByCompany(company, pageable);
        return paginationService.paginate(recruiters, r -> MemberResponse.builder()
                .id(r.getUserId())
                .avatar(r.getCompany().getLogo())
                .email(r.getEmail())
                .fullname(r.getFullname())
                .contact(r.getContact())
                .role(r.isRepresentative() ? "representative" : "member")
                .build());
    }

    public PaginatedResponse<AllInvitationResponse> getInvitations(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Recruiter recruiter = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(RoleService.class, userId));
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NOT_IN_COMPANY);
        }
        Page<Invitation> invitations = invitationRepository.findAllByCompany(company, pageable);
        return paginationService.paginate(invitations, i -> AllInvitationResponse.builder()
                .id(i.getId())
                .email(i.getEmail())
                .expiryDate(i.getExpiryDate())
                .accepted(i.isAccepted())
                .inviterName(i.getInviter().getFullname())
                .build());
    }

    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(Company.class, id));
        return companyMapper.toCompanyResponse(company);
    }

    public void removeMember(Long userId, long id) {
        if (userId.equals(id)) {
            throw new AppException(ErrorCode.NOT_PERMITTED);
        }
        Recruiter representative = (Recruiter) userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, userId));

        if (!representative.isRepresentative()) {
            throw new AppException(ErrorCode.NOT_PERMITTED);
        }

        Company company = representative.getCompany();
        if (company == null) {
            throw new AppException(ErrorCode.NOT_IN_COMPANY);
        }
        Recruiter member = (Recruiter) userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Recruiter.class, id));
        if (!member.getCompany().getCompanyId().equals(company.getCompanyId())) {
            throw new AppException(ErrorCode.NOT_PERMITTED);
        }
        member.setCompany(null);

        userRepository.save(member);
    }
}
