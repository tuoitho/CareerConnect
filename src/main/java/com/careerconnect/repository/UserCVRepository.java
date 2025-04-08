package com.careerconnect.cv;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserCVRepository extends JpaRepository<UserCV, Long> {
    List<UserCV> findByUserId(Long userId);
    void deleteByUserIdAndId(Long userId, Long id);
}