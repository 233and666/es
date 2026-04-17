package com.example.esspringboot.config;

import com.example.esspringboot.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(jwtInterceptor)

                .addPathPatterns(
                       "/api/user/**",
                       "/api/favorite/**",
                       "/api/order/**",
                       "/api/message/**",
                       "/api/product/**"
                )

                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",  // 排除登录注册
                        "/api/product/detail/*",       // 商品详情（公开访问）
                        "/api/product/search",         // 商品搜索（公开访问）
                        "/uploads/**"
                        );
    }

//    前端
//  ↓ (发送请求)
//    服务器接收HTTP请求
//  ↓
//    Spring MVC框架路由
//  ↓
//    WebConfig路径匹配 → 匹配成功，需要JWT拦截
//  ↓
//          JwtInterceptor.preHandle()
//          ↓ (Token验证)
//            ├── 成功 → 设置userId属性 → 放行请求
//  └── 失败 → 返回401错误 → 流程结束
//  ↓ (验证通过)
//            UserController.updatePassword()
//            ↓
//    从request获取userId
//  ↓
//    参数验证和业务处理
//  ↓
//    数据库操作
//  ↓
//    返回结果给Spring MVC
//  ↓
//    封装HTTP响应
//  ↓
//    返回给前端
//
//
}