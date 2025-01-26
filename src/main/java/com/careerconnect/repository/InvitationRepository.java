package com.careerconnect.repository;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.Invitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByToken(String token);

    Page<Invitation> findAllByCompany(Company company, Pageable pageable);
}
