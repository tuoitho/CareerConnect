package com.careerconnect.controller;

import com.careerconnect.config.VNPayConfig;
import com.careerconnect.dto.common.ApiResponse;
import com.careerconnect.entity.CoinRecharge;
import com.careerconnect.service.impl.CoinRechargeService;
import com.careerconnect.util.AuthenticationHelper;
import com.careerconnect.util.Logger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
public class VNPayApiController {

    private final CoinRechargeService coinRechargeService;
    private final AuthenticationHelper authenticationHelper;
    private final VNPayConfig vnPayConfig;

    @Value("${frontend.url}")
    private String frontendUrl;
    private String paymentResultPath="/payment-result";

    @PostMapping("/create-payment")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPayment(
            @RequestParam("coinAmount") Integer coinAmount,
            HttpServletRequest request) throws Exception {

        Long userId=authenticationHelper.getUserId();
        // Tạo giao dịch nạp xu
        long amount= coinAmount*1000;
        CoinRecharge recharge = coinRechargeService.createRecharge(userId, coinAmount, (double)amount, "VNPAY");

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "order-type";
        String vnp_TxnRef = recharge.getId().toString();
        String vnp_IpAddr = request.getRemoteAddr();
        String vnp_TmnCode = vnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", (amount*100)+"");
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");

        String urlReturn = "";
        urlReturn += vnPayConfig.vnp_ReturnUrl;

        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String salt = vnPayConfig.vnp_HashSecret;
        Logger.log(salt);
        String vnp_SecureHash = VNPayConfig.hmacSHA512(salt, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        Map<String, String> result = new HashMap<>();
        result.put("paymentUrl", vnPayConfig.vnp_PayUrl + "?" + queryUrl);
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .result(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment-return")
    public ResponseEntity<Void> paymentReturn(HttpServletRequest request) throws Exception {
        // Khởi tạo Map để lưu trữ tham số
        Map<String, String> fields = new HashMap<>();

        // Lấy và mã hóa tất cả tham số từ request
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        // Lấy vnp_SecureHash từ request và loại bỏ các trường không cần thiết
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // Tính checksum bằng hàm hashAllFields
        String signValue = vnPayConfig.hashAllFields(fields);
        String vnp_TxnRef = fields.get("vnp_TxnRef");

        // Chuẩn bị URL chuyển hướng
        String redirectUrl = frontendUrl + paymentResultPath;
        String encodedTxnRef = URLEncoder.encode(vnp_TxnRef != null ? vnp_TxnRef : "", StandardCharsets.UTF_8);
        redirectUrl += "?txnRef=" + encodedTxnRef;

        // Kiểm tra checksum và trạng thái giao dịch
        Map<String, String> response = new HashMap<>();
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
//                update
                coinRechargeService.completeRecharge(Long.parseLong(Objects.requireNonNull(vnp_TxnRef)));
                Logger.log("Payment success for transaction: " + vnp_TxnRef);
                response.put("status", "success");
                response.put("orderInfo", fields.get("vnp_OrderInfo"));
                response.put("amount", String.valueOf(Long.parseLong(fields.get("vnp_Amount")) / 100));
                response.put("transactionId", fields.get("vnp_TransactionNo"));
                redirectUrl += "&status=success";
            } else {
                coinRechargeService.failRecharge(Long.parseLong(Objects.requireNonNull(vnp_TxnRef)));
                Logger.log("Payment failed for transaction: " + vnp_TxnRef + " with status: " + request.getParameter("vnp_TransactionStatus"));
                response.put("status", "failed");
                response.put("message", "Giao dịch không thành công");
                redirectUrl += "&status=failed";
            }
        } else {
            Logger.log("Checksum mismatch for transaction: " + vnp_TxnRef);
            response.put("status", "failed");
            response.put("message", "Checksum không hợp lệ");
            redirectUrl += "&status=failed&error=checksum";
        }

        // Ghi log dữ liệu phản hồi và URL
        Logger.log("Response data: " + response);
        Logger.log("Redirecting to: " + redirectUrl);

        // Tạo redirect response
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new java.net.URI(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Found
    }
}