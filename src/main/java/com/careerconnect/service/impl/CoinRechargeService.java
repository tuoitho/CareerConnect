package com.careerconnect.service.impl;

import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.CoinRechargeResponse;
import com.careerconnect.entity.CoinRecharge;
import com.careerconnect.entity.User;
import com.careerconnect.exception.ResourceNotFoundException;
import com.careerconnect.repository.CoinRechargeRepository;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.service.PaginationService;
import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoinRechargeService {
    private final CoinRechargeRepository coinRechargeRepository;
    private final UserRepository userRepository;
    private final PaginationService paginationService;

    @Transactional
    public CoinRecharge createRecharge(Long userId, Integer coinAmount, Double amountPaid, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        CoinRecharge recharge = new CoinRecharge();
        recharge.setUser(user);
        recharge.setCoinAmount(coinAmount);
        recharge.setAmountPaid(amountPaid);
        recharge.setPaymentMethod(paymentMethod);
        recharge.setTransactionCode(UUID.randomUUID().toString()); // Sinh mã giao dịch ngẫu nhiên
        recharge.setStatus("PENDING"); // Ban đầu là PENDING

        return coinRechargeRepository.save(recharge);
    }

    @Transactional
    public void completeRecharge(Long rechargeId) {
        CoinRecharge recharge = coinRechargeRepository.findById(rechargeId)
                .orElseThrow(() -> new RuntimeException("Recharge not found"));
        recharge.setStatus("SUCCESS");
        recharge.setUpdatedAt(LocalDateTime.now());

        // Cập nhật số xu cho user
        User user = recharge.getUser();
        user.setCoinBalance(user.getCoinBalance() + recharge.getCoinAmount());
        userRepository.save(user);

        coinRechargeRepository.save(recharge);
    }

    @Transactional
    public void failRecharge(Long rechargeId) {
        CoinRecharge recharge = coinRechargeRepository.findById(rechargeId)
                .orElseThrow(() -> new RuntimeException("Recharge not found"));

        if (!"PENDING".equals(recharge.getStatus())) {
            throw new RuntimeException("Recharge is already processed");
        }

        recharge.setStatus("FAILED");
        recharge.setUpdatedAt(LocalDateTime.now());

        coinRechargeRepository.save(recharge);
    }

    public PaginatedResponse<CoinRechargeResponse> getUserRechargeHistory(Long userId, Pageable pageable) {
        Page<CoinRecharge> page = coinRechargeRepository.findAllByUser_UserId(userId, pageable);
        Logger.log("CoinRecharge history", page);
        return paginationService.paginate(page, x-> CoinRechargeResponse.builder()
                .id(x.getId())
                .coinAmount(x.getCoinAmount())
                .amountPaid(x.getAmountPaid())
                .paymentMethod(x.getPaymentMethod())
                .transactionCode(x.getTransactionCode())
                .status(x.getStatus())
                .createdAt(x.getCreatedAt())
                .updatedAt(x.getUpdatedAt())
                .build());
    }

    public Integer getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
        return user.getCoinBalance();
    }

    public PaginatedResponse<CoinRechargeResponse> getAllRechargeHistory(Pageable pageable){
        return paginationService.paginate(coinRechargeRepository.findAll(pageable),x-> CoinRechargeResponse.builder()
                .id(x.getId())
                .coinAmount(x.getCoinAmount())
                .amountPaid(x.getAmountPaid())
                .paymentMethod(x.getPaymentMethod())
                .transactionCode(x.getTransactionCode())
                .status(x.getStatus())
                .createdAt(x.getCreatedAt())
                .updatedAt(x.getUpdatedAt())
                .build());

    }
}