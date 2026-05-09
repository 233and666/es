package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.esspringboot.entity.Admin;
import com.example.esspringboot.service.IAdminService;
import com.example.esspringboot.util.JwtUtil;
import com.example.esspringboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 管理员表 前端控制器
 * </p>
 *
 * @author your_name
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Admin loginAdmin) {
        System.out.println("管理员登录请求=========");

        String username = loginAdmin.getUsername();
        String password = loginAdmin.getPassword();

        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminService.getOne(queryWrapper);

        if (admin == null) {
            return Result.error("管理员不存在");
        }

        if (!admin.getPassword().equals(password)) {
            return Result.error("密码错误");
        }

        if (admin.getStatus() == null || !admin.getStatus()) {
            return Result.error("账号已被禁用");
        }

        String token = JwtUtil.generateToken(admin.getId(), admin.getUsername());
        admin.setPassword(null);

        Map<String, Object> result = new HashMap<>();
        result.put("admin", admin);
        result.put("token", token);

        System.out.println("管理员登录成功 - 用户ID: " + admin.getId() + ", 用户名: " + admin.getUsername());
        return Result.success(result, "登录成功");
    }

}









