package com.careerconnect.cv.repository;

import com.careerconnect.cv.model.UserCV;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CVRepository extends JpaRepository<UserCV, Long> {
}