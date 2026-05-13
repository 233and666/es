package com.example.esspringboot.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtil {

    // JWT密钥（生产环境应该从配置文件中读取）
    private static final String SECRET_KEY = "es-springboot-jwt-secret-key-2026-04-15";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Token过期时间（24小时）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)// 设置自定义声明信息
                .setSubject(username)// 设置主题为用户名
                .setIssuedAt(new Date())// 设置签发时间为当前时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))// 设置过期时间为当前时间加上过期时间
                .signWith(KEY, SignatureAlgorithm.HS256)// 使用HMAC-SHA256算法和密钥签名
                .compact();// 生成最终的JWT字符串
    }

    /**
     * 验证JWT Token
     * @param token JWT Token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * 检查Token是否即将过期（剩余时间小于30分钟）
     * @param token JWT Token
     * @return 是否即将过期
     */
    public static boolean isTokenExpiring(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            return remainingTime < 30 * 60 * 1000; // 30分钟
        } catch (JwtException e) {
            return true;
        }
    }
}