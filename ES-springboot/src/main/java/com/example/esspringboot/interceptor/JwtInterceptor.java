package com.example.esspringboot.interceptor;

import com.example.esspringboot.util.JwtUtil;
import com.example.esspringboot.tokenBlack.TokenBlacklist;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 创建JWT拦截器类
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenBlacklist tokenBlacklist;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        System.out.println("拦截请求路径: " + request.getRequestURI());


        // 从请求头中获取JWT
               String token = request.getHeader("Authorization");
        // 验证JWT是否有效
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            System.out.println("获取到的JWT: " + token);//=========================

            // 验证JWT是否有效且不在黑名单黑名单中
            if (JwtUtil.validateToken(token) && !tokenBlacklist.isBlacklisted(token)) {
                // 将用户信息存入请求属性，供Controller使用
                Long userId = JwtUtil.getUserIdFromToken(token);
                request.setAttribute("userId", userId);
                System.out.println("用户ID: " + userId+"已经放行请求====");//=========================
                return true;// 放行请求
            }else {
                // Token无效或在黑名单中
                System.out.println("Token无效或已退出登录=============");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());// 401错误
                response.getWriter().write("{\"error\":\"Token无效或已退出登录\"}");// 返回错误信息
                                    //转义后的 JSON 字符串，实际内容为：{"error":"Token无效或已退出登录"}。
                return false;
            }
        }
        // 如果JWT无效，返回401错误
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        System.out.println("JWT无效或未提供=============");
        return false;// 拒绝请求
    }
}