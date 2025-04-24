package com.careerconnect.repository;

import com.careerconnect.entity.CoinRecharge;
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
public interface CoinRechargeRepository extends JpaRepository<CoinRecharge, Long> {
    List<CoinRecharge> findByStatus(String status);

    Page<CoinRecharge> findAllByUser_UserId(Long userId, Pageable pageable);

    @Query("SELECT SUM(cr.amountPaid) FROM CoinRecharge cr WHERE cr.status = :status AND cr.createdAt BETWEEN :start AND :end")
    double sumAmountPaidByStatusAndDateRange(
            @Param("status") String status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT cr FROM CoinRecharge cr WHERE " +
            "(:searchTerm IS NULL OR cr.transactionCode LIKE %:searchTerm% OR cr.user.email LIKE %:searchTerm%) AND " +
            "(:status IS NULL OR cr.status = :status) AND " +
            "(:fromDate IS NULL OR cr.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR cr.createdAt <= :toDate)")
    Page<CoinRecharge> findTransactionsByFilters(
            @Param("searchTerm") String searchTerm,
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    // Thống kê doanh thu theo ngày
    @Query("SELECT FUNCTION('DATE', cr.createdAt) as date, SUM(cr.amountPaid) as total " +
            "FROM CoinRecharge cr " +
            "WHERE cr.status = 'SUCCESS' AND cr.createdAt >= :from AND cr.createdAt < :to " +
            "GROUP BY FUNCTION('DATE', cr.createdAt) " +
            "ORDER BY FUNCTION('DATE', cr.createdAt)")
    List<Object[]> sumRevenueByDayRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Double> sumRevenueByDay(LocalDate from, LocalDate to) {
        List<Object[]> results = sumRevenueByDayRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).doubleValue()
                ));
    }

    // Thống kê doanh thu theo tuần (sửa GROUP BY và ORDER BY)
    @Query("SELECT CONCAT(YEAR(cr.createdAt), '-', FUNCTION('WEEK', cr.createdAt)) as week, SUM(cr.amountPaid) as total " +
            "FROM CoinRecharge cr " +
            "WHERE cr.status = 'SUCCESS' AND cr.createdAt >= :from AND cr.createdAt < :to " +
            "GROUP BY CONCAT(YEAR(cr.createdAt), '-', FUNCTION('WEEK', cr.createdAt)) " +
            "ORDER BY CONCAT(YEAR(cr.createdAt), '-', FUNCTION('WEEK', cr.createdAt))")
    List<Object[]> sumRevenueByWeekRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Double> sumRevenueByWeek(LocalDate from, LocalDate to) {
        List<Object[]> results = sumRevenueByWeekRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).doubleValue()
                ));
    }

    // Thống kê doanh thu theo tháng
    @Query("SELECT CONCAT(YEAR(cr.createdAt), '-', LPAD(CAST(MONTH(cr.createdAt) AS string), 2, '0')) as month, SUM(cr.amountPaid) as total " +
            "FROM CoinRecharge cr " +
            "WHERE cr.status = 'SUCCESS' AND cr.createdAt >= :from AND cr.createdAt < :to " +
            "GROUP BY CONCAT(YEAR(cr.createdAt), '-', LPAD(CAST(MONTH(cr.createdAt) AS string), 2, '0')) " +
            "ORDER BY CONCAT(YEAR(cr.createdAt), '-', LPAD(CAST(MONTH(cr.createdAt) AS string), 2, '0'))")
    List<Object[]> sumRevenueByMonthRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    default Map<String, Double> sumRevenueByMonth(LocalDate from, LocalDate to) {
        List<Object[]> results = sumRevenueByMonthRaw(from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return results.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).doubleValue()
                ));
    }
}