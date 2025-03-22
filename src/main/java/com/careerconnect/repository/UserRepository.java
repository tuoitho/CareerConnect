package com.careerconnect.repository;

import com.careerconnect.entity.Recruiter;
import com.careerconnect.entity.User;
import com.careerconnect.enums.RoleEnum;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countByLockedFalse();
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :roleName")
    long countByRole_RoleName(RoleEnum roleName);
}