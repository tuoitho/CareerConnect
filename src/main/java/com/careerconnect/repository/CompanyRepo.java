package com.careerconnect.repository;

import com.careerconnect.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepo extends JpaRepository<Company,Long>{
//    countByApprovedTrue
    long countByApprovedTrue();
    long countByApprovedFalse();

}
