package com.careerconnect.repository;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.Job;
import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface JobRepo extends JpaRepository<Job, Long> {
    Page<Job> findAllByCompany(Company company, Pageable pageable);

    Page<Job> findAllByCompanyAndActiveTrue(Company company, Pageable pageable);

    Page<Job> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String query, String query1, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE " +
            "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:area IS NULL OR LOWER(j.area) LIKE LOWER(CONCAT('%', :area, '%'))) " +
            "AND (:jobType IS NULL OR j.type = :jobType) " +
            "AND (:experience IS NULL OR j.experience = :experience) " +
            "AND (:category IS NULL OR LOWER(j.category) LIKE LOWER(CONCAT('%', :category, '%'))) " +
            "AND (:minSalary IS NULL OR CAST(j.minSalary AS double) >= :minSalary) " +
            "AND (:maxSalary IS NULL OR CAST(j.maxSalary AS double) <= :maxSalary) " +
            "AND j.active = true")
    Page<Job> searchJobs(
            @Param("keyword") String keyword,
            @Param("area") String area,
            @Param("jobType") JobTypeEnum jobType,
            @Param("experience") ExpEnum experience,
            @Param("category") String category,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            Pageable pageable
    );

    // Thống kê job postings theo ngày
    @Query("SELECT FUNCTION('DATE', j.created) as date, COUNT(j) as count " +
            "FROM Job j " +
            "WHERE j.created >= :from AND j.created < :to " +
            "GROUP BY FUNCTION('DATE', j.created) " +
            "ORDER BY FUNCTION('DATE', j.created)")
    List<Object[]> countJobPostingsByDayRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Integer> countJobPostingsByDay(LocalDate from, LocalDate to) {
        List<Object[]> results = countJobPostingsByDayRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).intValue()
                ));
    }

    // Thống kê job postings theo tuần (sửa GROUP BY và ORDER BY)
    @Query("SELECT CONCAT(YEAR(j.created), '-', FUNCTION('WEEK', j.created)) as week, COUNT(j) as count " +
            "FROM Job j " +
            "WHERE j.created >= :from AND j.created < :to " +
            "GROUP BY CONCAT(YEAR(j.created), '-', FUNCTION('WEEK', j.created)) " +
            "ORDER BY CONCAT(YEAR(j.created), '-', FUNCTION('WEEK', j.created))")
    List<Object[]> countJobPostingsByWeekRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Integer> countJobPostingsByWeek(LocalDate from, LocalDate to) {
        List<Object[]> results = countJobPostingsByWeekRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).intValue()
                ));
    }

    // Thống kê job postings theo tháng
    @Query("SELECT CONCAT(YEAR(j.created), '-', LPAD(CAST(MONTH(j.created) AS string), 2, '0')) as month, COUNT(j) as count " +
            "FROM Job j " +
            "WHERE j.created >= :from AND j.created < :to " +
            "GROUP BY CONCAT(YEAR(j.created), '-', LPAD(CAST(MONTH(j.created) AS string), 2, '0')) " +
            "ORDER BY CONCAT(YEAR(j.created), '-', LPAD(CAST(MONTH(j.created) AS string), 2, '0'))")
    List<Object[]> countJobPostingsByMonthRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Integer> countJobPostingsByMonth(LocalDate from, LocalDate to) {
        List<Object[]> results = countJobPostingsByMonthRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).intValue()
                ));
    }
}