package com.careerconnect.cv;

import com.careerconnect.entity.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CVRepository extends JpaRepository<UserCV, Long> {
    List<UserCV> findByUserId(Long userId);
    void deleteByUserIdAndId(Long userId, Long id);
}