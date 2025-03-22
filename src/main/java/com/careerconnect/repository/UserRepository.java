package com.careerconnect.repository;

import com.careerconnect.entity.User;
import com.careerconnect.enums.RoleEnum;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countByLockedFalse();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :roleName")
    long countByRole_RoleName(RoleEnum roleName);

    @Query("SELECT u FROM User u WHERE " +
            "(:searchTerm IS NULL OR u.username LIKE %:searchTerm% OR u.email LIKE %:searchTerm%) AND " +
            "(:userType IS NULL OR u.role.roleName = :userType) AND " +
            "(:status IS NULL OR u.active = :status)")
    Page<User> findUsersByFilters(
            @Param("searchTerm") String searchTerm,
            @Param("userType") String userType,
            @Param("status") Boolean status,
            Pageable pageable);

    // Thống kê đăng ký người dùng theo ngày
    @Query("SELECT FUNCTION('DATE', u.createdAt) as date, COUNT(u) as count " +
            "FROM User u " +
            "WHERE u.createdAt >= :from AND u.createdAt < :to " +
            "GROUP BY FUNCTION('DATE', u.createdAt) " +
            "ORDER BY FUNCTION('DATE', u.createdAt)")
    List<Object[]> countUserRegistrationsByDayRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Integer> countUserRegistrationsByDay(LocalDate from, LocalDate to) {
        List<Object[]> results = countUserRegistrationsByDayRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).intValue()
                ));
    }

    // Thống kê đăng ký người dùng theo tuần (sửa GROUP BY và ORDER BY)
    @Query("SELECT CONCAT(YEAR(u.createdAt), '-', FUNCTION('WEEK', u.createdAt)) as week, COUNT(u) as count " +
            "FROM User u " +
            "WHERE u.createdAt >= :from AND u.createdAt < :to " +
            "GROUP BY CONCAT(YEAR(u.createdAt), '-', FUNCTION('WEEK', u.createdAt)) " +
            "ORDER BY CONCAT(YEAR(u.createdAt), '-', FUNCTION('WEEK', u.createdAt))")
    List<Object[]> countUserRegistrationsByWeekRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Integer> countUserRegistrationsByWeek(LocalDate from, LocalDate to) {
        List<Object[]> results = countUserRegistrationsByWeekRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).intValue()
                ));
    }

    // Thống kê đăng ký người dùng theo tháng
    @Query("SELECT CONCAT(YEAR(u.createdAt), '-', LPAD(CAST(MONTH(u.createdAt) AS string), 2, '0')) as month, COUNT(u) as count " +
            "FROM User u " +
            "WHERE u.createdAt >= :from AND u.createdAt < :to " +
            "GROUP BY CONCAT(YEAR(u.createdAt), '-', LPAD(CAST(MONTH(u.createdAt) AS string), 2, '0')) " +
            "ORDER BY CONCAT(YEAR(u.createdAt), '-', LPAD(CAST(MONTH(u.createdAt) AS string), 2, '0'))")
    List<Object[]> countUserRegistrationsByMonthRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Integer> countUserRegistrationsByMonth(LocalDate from, LocalDate to) {
        List<Object[]> results = countUserRegistrationsByMonthRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).intValue()
                ));
    }
}