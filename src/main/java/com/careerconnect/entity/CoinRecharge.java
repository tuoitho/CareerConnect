package com.careerconnect.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "coin_recharges")
@Data
public class CoinRecharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Liên kết với người dùng thực hiện nạp xu

    @Column(name = "coin_amount", nullable = false)
    private Integer coinAmount; // Số xu nạp

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid; // Số tiền thực tế thanh toán (VND, USD, ...)

    @Column(name = "payment_method")
    private String paymentMethod; // Phương thức thanh toán (ví dụ: "Momo", "BankCard", "PayPal")

    @Column(name = "transaction_code", unique = true)
    private String transactionCode; // Mã giao dịch (từ cổng thanh toán hoặc tự sinh)

    @Column(name = "status", nullable = false)
    private String status; // Trạng thái giao dịch: "PENDING", "SUCCESS", "FAILED"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo giao dịch

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Thời gian cập nhật (khi hoàn tất hoặc thất bại)
}