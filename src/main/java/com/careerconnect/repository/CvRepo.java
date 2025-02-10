package com.careerconnect.repository;

import com.careerconnect.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CvRepo extends JpaRepository<CV, Long> {
}
