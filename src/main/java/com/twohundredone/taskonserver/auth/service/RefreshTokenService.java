package com.twohundredone.taskonserver.auth.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    private String getKey(Long userId) {
        return "refresh:" + userId;
    }

    public void save(Long userId, String refreshToken, long ttlMillis) {
        String key = getKey(userId);
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofMillis(ttlMillis)
        );
    }

    public String get(Long userId) {
        String key = getKey(userId);
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(Long userId) {
        String key = getKey(userId);
        redisTemplate.delete(key);
    }
}
