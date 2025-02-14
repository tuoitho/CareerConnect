package com.careerconnect.repository;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepo extends JpaRepository<Job, Long> {
    Page<Job> findAllByCompany(Company company,Pageable pageable);
    Page<Job> findAllByCompanyAndActiveTrue(Company company,Pageable pageable);

    Page<Job> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String query, String query1, Pageable pageable);
}
