package com.careerconnect.repository;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.Job;
import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepo extends JpaRepository<Job, Long> {
    Page<Job> findAllByCompany(Company company,Pageable pageable);
    Page<Job> findAllByCompanyAndActiveTrue(Company company,Pageable pageable);

    Page<Job> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String query, String query1, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE " +
            "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:area IS NULL OR LOWER(j.area) LIKE LOWER(CONCAT('%', :area, '%'))) " +
            "AND (:jobType IS NULL OR j.type = :jobType) " +
            "AND (:expereince IS NULL OR j.experience = :expereince) " +
            "AND (:category IS NULL OR LOWER(j.category) LIKE LOWER(CONCAT('%', :category, '%'))) " +
            "AND (:minSalary IS NULL OR CAST(j.minSalary AS double) >= :minSalary) " +
            "AND (:maxSalary IS NULL OR CAST(j.maxSalary AS double) <= :maxSalary) " +
            "AND j.active = true")
    Page<Job> searchJobs(
            @Param("keyword") String keyword,
            @Param("area") String area,
            @Param("jobType") JobTypeEnum jobType,
            @Param("expereince") ExpEnum expereince,
            @Param("category") String category,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            Pageable pageable
    );
}
