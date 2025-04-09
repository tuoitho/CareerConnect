package com.CareerConnect.model.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignalMessage {
    private String type;    // Loại tín hiệu: "offer", "answer", "ice-candidate"
    private String from;    // ID của người gửi
    private String to;      // ID của người nhận
    private Object data;    // Dữ liệu tín hiệu (SDP offer/answer hoặc ICE candidate)
    private String roomId;  // ID của phòng phỏng vấn
}