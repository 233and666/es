package com.example.esspringboot.tokenBlack;


import com.example.esspringboot.util.JwtUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//========== 用于存储和管理失效的JWT Token黑名单=========
//适用范围：
//      单机部署
//      用户量较小
//      开发测试环境
@Component
public class TokenBlacklist {
    // 使用线程安全的Set存储黑名单Token
    private Set<String> blacklist = Collections.synchronizedSet(new HashSet<>());

    /**
     * 将Token加入黑名单
     * @param token 需要失效的JWT Token
     */
    public void addToBlacklist(String token) {
        blacklist.add(token);
        System.out.println("Token已加入黑名单: " + token.substring(0, 20) + "...");
    }

    /**
     * 检查Token是否在黑名单中
     * @param token 需要验证的JWT Token
     * @return true=在黑名单中，false=不在黑名单
     */
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }

    /**
     * 从黑名单中移除Token（用于Token自然过期后的清理）
     * @param token 需要移除的Token
     */
    public void removeFromBlacklist(String token) {
        blacklist.remove(token);
    }

    /**
     * 定时清理过期的黑名单Token（避免内存泄漏）
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000)//要在启动类添加@EnableScheduling
    public void cleanupExpiredTokens() {
        int initialSize = blacklist.size();
        Iterator<String> iterator = blacklist.iterator();

        while (iterator.hasNext()) {// 遍历黑名单中的Token
            String token = iterator.next();// 获取当前Token
            try {
                // 如果Token已过期，从黑名单中移除
                if (!JwtUtil.validateToken(token)) {// 验证Token是否过期
                    iterator.remove();
                    System.out.println("清理过期黑名单Token: " + token.substring(0, 20) + "...");
                }
            } catch (Exception e) {
                // 解析失败的Token也移除
                iterator.remove();
            }
        }

        System.out.println("黑名单清理完成，清理前: " + initialSize + "，清理后: " + blacklist.size());
    }
}
