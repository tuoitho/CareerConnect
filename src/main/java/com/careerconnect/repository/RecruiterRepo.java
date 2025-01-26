package com.careerconnect.repository;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.Recruiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterRepo extends JpaRepository<Recruiter, Long> {
    Page<Recruiter> findAllByCompany(Company company, Pageable pageable);
}
