package com.careerconnect.repository;

import com.careerconnect.entity.UserCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserCVRepository extends JpaRepository<UserCV, Long> {
    List<UserCV> findByUserId(Long userId);
    void deleteByUserIdAndId(Long userId, Long id);
}