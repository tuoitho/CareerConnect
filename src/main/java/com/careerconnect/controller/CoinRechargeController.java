package com.careerconnect.controller;

import com.careerconnect.constant.SecurityEndpoint;
import com.careerconnect.dto.common.ApiResp;
import com.careerconnect.dto.common.PaginatedResponse;
import com.careerconnect.dto.response.CoinRechargeResponse;
import com.careerconnect.repository.UserRepository;
import com.careerconnect.service.impl.CoinRechargeService;
import com.careerconnect.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/coin-recharges")
@RequiredArgsConstructor
@PreAuthorize(SecurityEndpoint.BOTH)
public class CoinRechargeController {
    private final CoinRechargeService coinRechargeService;
    private final AuthenticationHelper authenticationHelper;
    private final UserRepository userRepository;

//    // Tạo yêu cầu nạp xu
//    @PostMapping("/create")
//    public ResponseEntity<?> createRecharge(
//            @RequestParam Long userId,
//            @RequestParam Integer coinAmount,
//            @RequestParam Double amountPaid,
//            @RequestParam String paymentMethod) {
//        CoinRecharge recharge = coinRechargeService.createRecharge(userId, coinAmount, amountPaid, paymentMethod);
//        ApiResponse<?> response = ApiResponse.builder()
//                .message("Recharge created successfully")
//                .result(recharge)
//                .build();
//        return ResponseEntity.ok(response);
//    }

    // Hoàn tất giao dịch (giả lập callback từ cổng thanh toán)
//    @PutMapping("/complete/{rechargeId}")
//    public ResponseEntity<CoinRecharge> completeRecharge(@PathVariable Long rechargeId) {
//        CoinRecharge recharge = coinRechargeService.completeRecharge(rechargeId);
//        return ResponseEntity.ok(recharge);
//    }

    // Đánh dấu giao dịch thất bại
//    @PutMapping("/fail/{rechargeId}")
//    public ResponseEntity<CoinRecharge> failRecharge(@PathVariable Long rechargeId) {
//        CoinRecharge recharge = coinRechargeService.failRecharge(rechargeId);
//        return ResponseEntity.ok(recharge);
//    }

    // Get recharge history with pagination
    @GetMapping("/recharge-history")
    public ResponseEntity<?> getRechargeHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Long userId = authenticationHelper.getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PaginatedResponse<CoinRechargeResponse> historyPage = coinRechargeService.getUserRechargeHistory(userId, pageable);

        ApiResp<?> response = ApiResp.builder()
                .result(historyPage)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/balance")
    public ResponseEntity<ApiResp<Integer>> getCoinBalance() {
        Long userId = authenticationHelper.getUserId();
        ApiResp<Integer> response = ApiResp.<Integer>builder()
                .result(coinRechargeService.getBalance(userId))
                .message("Coin balance retrieved successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
