package com.careerconnect.repository;

import com.careerconnect.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CandidateRepo extends JpaRepository<Candidate, Long> {
    @Query("SELECT c FROM Candidate c " +
            "LEFT JOIN FETCH c.educations " +
            "LEFT JOIN FETCH c.experiences " +
            "LEFT JOIN FETCH c.cvs " +
            "WHERE c.userId = :id")
    Optional<Candidate> findByIdWithRelations(@Param("id") Long id);
}
