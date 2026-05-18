package com.example.esspringboot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Component
public class RedisLockUtil {

    private static final String LOCK_PREFIX = "lock:";  // 锁前缀
    private static final long LOCK_EXPIRE_SECONDS = 30; // 锁过期时间30秒

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取分布式锁
     * @param lockKey 锁名称
     * @return 锁标识（用于释放锁）
     */
    public String acquireLock(String lockKey) {
        String lockId = UUID.randomUUID().toString();    // 生成随机唯一锁标识
        String key = LOCK_PREFIX + lockKey;               // 拼接完整的锁Key

        // 使用SET NX获取锁：只有当Key不存在时才设置成功,成功返回lockId
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, lockId, LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS); // 设置锁标识和过期时间
        return Boolean.TRUE.equals(result) ? lockId : null;
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁名称
     * @param lockId 锁标识
     */
    public void releaseLock(String lockKey, String lockId) {
        String key = LOCK_PREFIX + lockKey;
        String currentLockId = (String) redisTemplate.opsForValue().get(key);// 获取当前锁标识

        if (lockId.equals(currentLockId)) {// 如果当前锁标识与传入的锁标识一致
            redisTemplate.delete(key);// 删除锁标识
        }
    }
}