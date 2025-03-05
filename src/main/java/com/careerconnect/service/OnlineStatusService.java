//package com.careerconnect.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class OnlineStatusService {
//    //TODO
//    private final RedisTemplate<String, Boolean> redisTemplate;
//
//    public void setUserOnline(Long userId) {
//        redisTemplate.opsForValue().set("user:" + userId, true);
//    }
//
//    public void setUserOffline(Long userId) {
//        redisTemplate.opsForValue().set("user:" + userId, false);
//    }
//
//    public boolean isUserOnline(Long userId) {
//        Boolean online = redisTemplate.opsForValue().get("user:" + userId);
//        return online != null && online;
//    }
//}