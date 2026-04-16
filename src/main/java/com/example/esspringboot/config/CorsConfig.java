package com.example.esspringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端地址（你的Vue项目运行地址）

        config.addAllowedOriginPattern("*");// config.addAllowedOrigin("http://localhost:5173");

        // 允许所有请求方法（GET/POST/PUT/DELETE）
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许携带Cookie（简化版可开启）
        config.setAllowCredentials(true);
        // 预检请求缓存时间（减少重复校验）
        config.setMaxAge(3600L);

        // 配置哪些接口需要跨域（这里配置所有/api开头的接口）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}