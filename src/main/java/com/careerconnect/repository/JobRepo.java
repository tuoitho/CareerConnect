package com.careerconnect.repository;

import com.careerconnect.entity.Company;
import com.careerconnect.entity.Job;
import com.careerconnect.enums.ExpEnum;
import com.careerconnect.enums.JobTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface JobRepo extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {


    static Specification<Job> findJobsByCriteria(
            String title,
            String skills,
            String location,
            String jobType,
            String experience,
            Integer minSalary,
            Integer maxSalary,
            String category,
            String area) {

        return (root, query, criteriaBuilder) -> {
            // Start with a true predicate (no conditions)
            var predicate = criteriaBuilder.conjunction();

            // Add title/description condition if provided
            if (title != null && !title.isEmpty()) {
                var titlePredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + title.toLowerCase() + "%")
                );
                predicate = criteriaBuilder.and(predicate, titlePredicate);
            }

            // Add skills condition if provided
            if (skills != null && !skills.isEmpty()) {
                var skillPredicates = criteriaBuilder.disjunction();
                String[] skillArray = skills.split(" ");

                for (String skill : skillArray) {
                    if (!skill.trim().isEmpty()) {
                        var skillPredicate = criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + skill.toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + skill.toLowerCase() + "%")
                        );
                        skillPredicates = criteriaBuilder.or(skillPredicates, skillPredicate);
                    }
                }

                predicate = criteriaBuilder.and(predicate, skillPredicates);
            }

            // Add location condition if provided
            if (location != null && !location.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }

            // Add job type condition if provided
            if (jobType != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("type"), jobType));
            }

            // Add experience level condition if provided
            if (experience != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("experience"), experience));
            }

            // Add minimum salary condition if provided
            if (minSalary != null) {
                // Convert string minSalary to integer for comparison
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("minSalary"), minSalary));
            }

            // Add maximum salary condition if provided
            if (maxSalary != null) {
                // Convert string maxSalary to integer for comparison
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("maxSalary"), maxSalary));
            }

            // Add category condition if provided
            if (category != null && !category.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
            }

            // Add area condition if provided
            if (area != null && !area.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("area")), "%" + area.toLowerCase() + "%"));
            }

            // Add active and approved conditions
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.isTrue(root.get("active")));
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.isTrue(root.get("approved")));

            return predicate;
        };
    }

    Page<Job> findAllByCompany(Company company, Pageable pageable);

    Page<Job> findAllByActiveTrue(Pageable pageable);

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

    @Query("""
            SELECT j FROM Job j WHERE
                concat(LOWER(j.title), ' ', LOWER(j.description), ' ', LOWER(j.location), ' ', LOWER(j.category), ' ', LOWER(j.area), ' ', LOWER(j.type), ' ', LOWER(j.experience))
                      LIKE CONCAT('%', LOWER(:requirement), '%')
            """)
    Page<Job> findAllByCriteria(
            @Param("requirement") String requirement,
            Pageable pageable);

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

    @Query("SELECT j FROM Job j WHERE " +
            "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:skills IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :skills, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :skills, '%'))) " +
            "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:jobType IS NULL OR j.type = :jobType) " +
            "AND (:experience IS NULL OR j.experience = :experience) " +
            "AND (:minSalary IS NULL OR CAST(j.minSalary AS INTEGER) >= :minSalary) " +
            "AND (:maxSalary IS NULL OR CAST(j.maxSalary AS INTEGER) <= :maxSalary) " +
            "AND (:category IS NULL OR LOWER(j.category) LIKE LOWER(CONCAT('%', :category, '%'))) " +
            "AND (:area IS NULL OR LOWER(j.area) LIKE LOWER(CONCAT('%', :area, '%'))) " +
            "AND j.active = true AND j.approved = true")
    Page<Job> findByCriteria(
            @Param("title") String title,
            @Param("skills") String skills, // Combined skills as a single string
            @Param("location") String location,
            @Param("jobType") JobTypeEnum jobType,
            @Param("experience") ExpEnum experience,
            @Param("minSalary") Integer minSalary,
            @Param("maxSalary") Integer maxSalary,
            @Param("category") String category,
            @Param("area") String area,
            Pageable pageable);

    Page<Job> findAllByTitleContaining(String lowerCase, PageRequest pageRequest);
}