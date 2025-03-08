package com.careerconnect.security;

import com.careerconnect.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;

    public void addToBlacklist(String token) {
        redisTemplate.opsForValue().set(token, "blacklisted", Duration.ofMinutes(30)); // Chặn trong 30 phút
    }

    public boolean isBlacklisted(String token) {
//        return redisTemplate.hasKey(token);
//        tamj thoi return false de do ton chi phi cloud
        return false;
    }

}