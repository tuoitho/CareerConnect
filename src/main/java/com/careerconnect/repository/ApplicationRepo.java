package com.careerconnect.repository;

import com.careerconnect.entity.Application;
import com.careerconnect.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long> {
    boolean existsByJob(Job job);
}
