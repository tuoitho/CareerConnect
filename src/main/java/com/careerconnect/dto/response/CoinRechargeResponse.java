package com.careerconnect.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CoinRechargeResponse{
    private Long id;

    private Integer coinAmount; // Số xu nạp

    private Double amountPaid; // Số tiền thực tế thanh toán (VND, USD, ...)

    private String paymentMethod; // Phương thức thanh toán (ví dụ: "Momo", "BankCard", "PayPal")

    private String transactionCode; // Mã giao dịch (từ cổng thanh toán hoặc tự sinh)

    private String status; // Trạng thái giao dịch: "PENDING", "SUCCESS", "FAILED"

    private LocalDateTime createdAt; // Thời gian tạo giao dịch

    private LocalDateTime updatedAt; // Thời gian cập nhật (khi hoàn tất hoặc thất bại)
}
