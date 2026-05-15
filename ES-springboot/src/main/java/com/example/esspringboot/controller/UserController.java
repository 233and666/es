package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.esspringboot.entity.User;
import com.example.esspringboot.service.IUserService;
import com.example.esspringboot.util.JwtUtil;
import com.example.esspringboot.util.Result;
import com.example.esspringboot.tokenBlack.TokenBlacklist;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user,HttpServletRequest request) {
        System.out.println("注册请求路径: " + request.getRequestURI());
        // 步骤 1：基础校验（用户名、密码不能为空）
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        // 步骤 2：用 QueryWrapper 查询【用户名是否已存在】
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername()).last("limit 1");
        User user1 = userService.getOne(queryWrapper);
        // 步骤 3：如果用户名已存在，返回错误
        if(user1 != null){
            System.out.println("用户名已被注册，请更换");
            return Result.error("用户名已被注册，请更换");
        }
        // 步骤 4：保存新用户
        boolean saveSuccess = userService.save(user);

        return saveSuccess ? Result.success(user) : Result.error("注册失败!");

    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser, HttpSession session) {
        System.out.println("登录进来了========= ");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginUser.getUsername());
        User user = userService.getOne(queryWrapper);

        if (user == null) {
            return Result.error("用户名不存在");
        }
        if (!user.getPassword().equals(loginUser.getPassword())) {
            return Result.error("密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 1) {
            return Result.error("该账号已被封禁，请联系管理员！");
        }


//        session.setAttribute("loginUser", user);登录信息放到会话域

        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        //清空密码
        user.setPassword(null);
        // 返回用户信息和Token
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        System.out.println("登录成功========= ");
        return Result.success(result, "登录成功");
    }

    @PostMapping("/updatePassword")
    public Result<Object> updatePassword(@RequestBody Map<String, String> passwordData,
                                         HttpServletRequest request) {
        try {
            // 从请求属性中获取用户ID（由拦截器设置）
            Long userId = (Long) request.getAttribute("userId");

            if (userId == null) {
                return Result.error("用户未登录");
            }

            // 获取请求参数
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");

            // 参数校验
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return Result.error("原密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Result.error("新密码不能为空");
            }

            // 查询当前用户信息
            User currentUser = userService.getById(userId);
            if (currentUser == null) {
                return Result.error("用户不存在");
            }

            // 验证原密码
            if (!currentUser.getPassword().equals(oldPassword)) {
                return Result.error("原密码错误");
            }

            // 更新密码
            currentUser.setPassword(newPassword);
            boolean updateSuccess = userService.updateById(currentUser);

            if (updateSuccess) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }

        } catch (Exception e) {
            return Result.error("系统错误，请稍后重试");
        }
    }

    @Autowired
    private TokenBlacklist tokenBlacklist;
    @PostMapping("/logout")
    //用户退出登录 → 将Token加入黑名单 → 后续请求验证时检查黑名单 → 黑名单中的Token拒绝访问
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (JwtUtil.validateToken(token)) {
                // 记录退出日志
                Long userId = JwtUtil.getUserIdFromToken(token);
                String username = JwtUtil.getUsernameFromToken(token);

                System.out.println("用户退出登录 - 用户ID: " + userId + ", 用户名: " + username);

                // 关键步骤：将Token加入黑名单
                tokenBlacklist.addToBlacklist(token);

                return Result.success("退出登录成功，Token已立即失效");
            }
        }

        return Result.success("退出登录成功");
    }

    //获取用户信息
    @GetMapping("/info")
    public Result<User> getUserInfo( HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        User user = userService.getById(userId);
        // 清空密码
        if(user.getPassword()!=null){
            user.setPassword(null);
        }
        return Result.success(user);

    }


}