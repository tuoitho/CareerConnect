package com.careerconnect.security;

import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;

    public void addRefreshTokenToBlacklist(String token) {
        redisTemplate.opsForValue().set(token, "blacklisted", Duration.ofDays(30)); // Vi thoi gian song cua refresh token la 30 ngay
    }
    public void addAccessTokenToBlacklist(String token) {
        //tạm cmt
//        redisTemplate.opsForValue().set(token, "blacklisted", Duration.ofMinutes(120));
    }

    public boolean isBlacklisted(String token) {
//        return redisTemplate.hasKey(token);
//        tạm thời
        return false;
    }

}