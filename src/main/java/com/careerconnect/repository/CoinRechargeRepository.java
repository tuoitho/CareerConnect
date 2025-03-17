package com.careerconnect.repository;

import com.careerconnect.entity.CoinRecharge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinRechargeRepository extends JpaRepository<CoinRecharge, Long> {
    List<CoinRecharge> findByStatus(String status); // Tìm giao dịch theo trạng thái
    Page<CoinRecharge> findAllByUser_UserId(Long userId, Pageable pageable); // Tìm giao dịch theo user
}