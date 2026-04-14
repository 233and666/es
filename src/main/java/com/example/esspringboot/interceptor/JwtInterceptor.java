package com.example.esspringboot.interceptor;

import com.example.esspringboot.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 创建JWT拦截器类
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取JWT
               String token = request.getHeader("Authorization");
        // 验证JWT是否有效
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (JwtUtil.validateToken(token)) {
                // 将用户信息存入请求属性，供Controller使用
                Long userId = JwtUtil.getUserIdFromToken(token);
                request.setAttribute("userId", userId);
                return true;// 放行请求
            }
        }
        // 如果JWT无效，返回401错误
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;// 拒绝请求
    }
}