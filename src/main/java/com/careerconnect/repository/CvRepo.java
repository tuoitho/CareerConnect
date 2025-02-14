package com.careerconnect.repository;

import com.careerconnect.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CvRepo extends JpaRepository<CV, Long> {
    Optional<Set<CV>> findByCandidate_UserId(Long userId);
}
