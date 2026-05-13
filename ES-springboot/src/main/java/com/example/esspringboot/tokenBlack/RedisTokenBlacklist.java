package com.example.esspringboot.tokenBlack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

//============ Redis存储（生产环境推荐） ============

@Component
public class RedisTokenBlacklist {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Redis键前缀
    private static final String BLACKLIST_PREFIX = "blacklist:token:";
    // 添加到黑名单
    public void addToBlacklist(String token, long ttlSeconds) {
        String key = BLACKLIST_PREFIX + token;// 构建黑名单键
        redisTemplate.opsForValue().set(key, "blacklisted", ttlSeconds, TimeUnit.SECONDS);// 设置过期时间
    }
    // 检查是否在黑名单中
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;// 构建黑名单键
        return redisTemplate.hasKey(key);// 检查键是否存在
    }
    // Redis会自动处理过期，无需手动清理
}
