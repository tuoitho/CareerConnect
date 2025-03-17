package com.careerconnect.atest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {
    @GetMapping("/favicon.ico")
    public void returnNoFavicon() {
        // Không làm gì cả để bỏ qua request này
    }
}