package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.esspringboot.entity.Admin;
import com.example.esspringboot.entity.Order;
import com.example.esspringboot.entity.Product;
<<<<<<< HEAD
import com.example.esspringboot.entity.User;
import com.example.esspringboot.service.IAdminService;
import com.example.esspringboot.service.IOrderService;
import com.example.esspringboot.service.IProductService;
import com.example.esspringboot.service.IUserService;
=======
import com.example.esspringboot.service.IAdminService;
import com.example.esspringboot.service.IOrderService;
import com.example.esspringboot.service.IProductService;
>>>>>>> 34a88b57ccfc1c26f78e238321853fdce817fa40
import com.example.esspringboot.util.JwtUtil;
import com.example.esspringboot.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 34a88b57ccfc1c26f78e238321853fdce817fa40

/**
 * <p>
 * 管理员表 前端控制器
 * </p>
 *
 * @author your_name
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IOrderService orderService;

<<<<<<< HEAD
    @Autowired
    private IUserService userService;

=======
>>>>>>> 34a88b57ccfc1c26f78e238321853fdce817fa40
    /**
     * 管理员注册接口 - 只有超级管理员才能注册新管理员
     */
    @PostMapping("/register")
    public Result<Admin> register(@RequestBody Admin newAdmin, HttpServletRequest request) {
        System.out.println("管理员注册请求=========");

        // 从请求属性中获取当前登录管理员ID（由JWT拦截器设置）
        Long currentAdminId = (Long) request.getAttribute("userId");
        if (currentAdminId == null) {
            return Result.error("请先登录");
        }

        // 查询当前登录管理员信息
        Admin currentAdmin = adminService.getById(currentAdminId);
        if (currentAdmin == null) {
            return Result.error("管理员不存在");
        }

        // 检查是否为超级管理员（role = true 表示超级管理员）
        if (currentAdmin.getRole() == null || !currentAdmin.getRole()) {
            return Result.error("权限不足，只有超级管理员才能注册新管理员");
        }

        // 校验新管理员信息
        if (newAdmin.getUsername() == null || newAdmin.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (newAdmin.getPassword() == null || newAdmin.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        // 检查用户名是否已存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", newAdmin.getUsername());
        Admin existingAdmin = adminService.getOne(queryWrapper);
        if (existingAdmin != null) {
            return Result.error("用户名已被注册");
        }

        // 设置默认值：普通管理员（role = false）、状态启用（status = true）
        if (newAdmin.getRole() == null) {
            newAdmin.setRole(false); // 默认普通管理员
        }
        if (newAdmin.getStatus() == null) {
            newAdmin.setStatus(true); // 默认启用
        }

        // 保存新管理员
        boolean saveSuccess = adminService.save(newAdmin);
        if (saveSuccess) {
            // 清空密码后返回
            newAdmin.setPassword(null);
            System.out.println("管理员注册成功 - 用户名: " + newAdmin.getUsername());
            return Result.success(newAdmin, "注册成功");
        } else {
            return Result.error("注册失败");
        }
    }

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

    //管理员获取商品列表
    @GetMapping("/list")
    public Result<IPage<Product>> adminProductList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "全部") String status,
            @RequestParam(required = false) String keyword) {
        
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        Page<Product> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        if (!status.equals("全部")) {
            queryWrapper.eq("status", status);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("title", keyword.trim());
        }
        
        queryWrapper.orderByDesc("create_time");
        IPage<Product> productIPage = productService.page(page, queryWrapper);
        System.out.println(productIPage);
        return Result.success(productIPage, "获取商品列表成功");
    }

    //管理员封禁/解封商品
    @PostMapping("/off/{id}")
    public Result<String> adminOffProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        if (product.getStatus().equals("在售")||product.getStatus().equals("下架")) {
            product.setStatus("封禁");
        } else if (product.getStatus().equals("封禁")) {
            product.setStatus("下架");
        } else if (product.getStatus().equals("已售")) {
            return Result.error("已售商品不能进行此操作");
        }
        
        boolean saveSuccess = productService.saveOrUpdate(product);
        if (saveSuccess) {
            return Result.success("商品操作成功");
        } else {
            return Result.error("操作失败");
        }
    }

    //管理员订单列表
    @GetMapping("/order/list")
    public Result<IPage<Order>> adminOrderList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "全部") String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        Page<Order> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if (!status.equals("全部")) {
            queryWrapper.eq("status", status);
        }
        
        if (userId != null) {
            queryWrapper.and(wrapper -> wrapper.eq("buyer_id", userId).or().eq("seller_id", userId));
        }
        
        if (startTime != null && !startTime.trim().isEmpty()) {
            queryWrapper.ge("create_time", startTime);
        }
        
        if (endTime != null && !endTime.trim().isEmpty()) {
            queryWrapper.le("create_time", endTime);
        }
        
        queryWrapper.orderByDesc("create_time");
        IPage<Order> orderIPage = orderService.page(page, queryWrapper);
        
        for (Order order : orderIPage.getRecords()) {
            Product product = productService.getById(order.getProductId());
            order.setProduct(product);
        }
        
        return Result.success(orderIPage, "获取订单列表成功");
    }

    //管理员查看订单详情
    @GetMapping("/order/detail/{id}")
    public Result<Order> adminOrderDetail(@PathVariable Long id) {

        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        Product product = productService.getById(order.getProductId());
        order.setProduct(product);

        System.out.println("订单详情: " + order);
        return Result.success(order, "获取订单详情成功");
    }
<<<<<<< HEAD

    @GetMapping("/users")
    public Result getAllUsers() {
        List<User> list = userService.list();
        return Result.success(list);

    }

    @PutMapping("/user/status/{id}")
    public Result updateUserStatus(@PathVariable long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        // 使用 MyBatis-Plus 的 updateById 方法
        boolean updated = userService.updateById(user);

        if (updated) {
            return Result.success("用户状态更新成功");
        } else {
            return Result.error("更新失败，请重试");
        }
    }
=======
>>>>>>> 34a88b57ccfc1c26f78e238321853fdce817fa40
    
}