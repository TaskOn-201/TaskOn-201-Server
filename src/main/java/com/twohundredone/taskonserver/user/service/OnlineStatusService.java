package com.twohundredone.taskonserver.user.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OnlineStatusService {
    private final StringRedisTemplate redisTemplate;

    private String key(Long userId) {
        return "online:user:" + userId;
    }

    // 로그인 시 호출
    public void setOnline(Long userId) {
        redisTemplate.opsForValue().set(
                key(userId),
                "true",
                Duration.ofMinutes(5)   // 5분 TTL
        );
    }

    // 활동 감지 시 호출 (API 요청 때 갱신)
    public void refresh(Long userId) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key(userId)))) {
            redisTemplate.expire(key(userId), Duration.ofMinutes(5));
        }
    }

    // 로그아웃 시 호출
    public void setOffline(Long userId) {
        redisTemplate.delete(key(userId));
    }

    // 조회용
    public boolean isOnline(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(userId)));
    }
}
