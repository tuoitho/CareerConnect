package com.careerconnect.repository;

import com.careerconnect.entity.CoinRecharge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoinRechargeRepository extends JpaRepository<CoinRecharge, Long> {
    List<CoinRecharge> findByStatus(String status); // Tìm giao dịch theo trạng thái
    Page<CoinRecharge> findAllByUser_UserId(Long userId, Pageable pageable); // Tìm giao dịch theo user


    @Query("SELECT SUM(cr.amountPaid) FROM CoinRecharge cr WHERE cr.status = :status AND cr.createdAt BETWEEN :start AND :end")
    double sumAmountPaidByStatusAndDateRange(@Param("status") String status,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}