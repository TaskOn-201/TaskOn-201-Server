package com.twohundredone.taskonserver.task.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskBoardCacheEvictor {
    private final StringRedisTemplate redisTemplate;

    public void evictProjectBoard(Long projectId) {
        // Spring Cache가 내부적으로 cacheName::key 형태로 저장하는 경우가 많음
        // 예: taskBoard::board:1:t=:p=:u=:a=false
        String pattern = "taskBoard::board:" + projectId + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
