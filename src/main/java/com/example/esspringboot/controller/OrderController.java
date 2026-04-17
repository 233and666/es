package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.esspringboot.entity.Order;
import com.example.esspringboot.entity.Product;
import com.example.esspringboot.service.IOrderService;
import com.example.esspringboot.service.IProductService;
import com.example.esspringboot.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private IProductService productService;

    //创建订单
    @PostMapping("/create")
    public Result<String> createOrder(@RequestParam("productId")Long productId, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        Order order = new Order();
        if (productId == null) {
            return Result.error("商品ID不能为空");
        }
        order.setProductId(productId);//商品id
        order.setBuyerId(userId);//买家id
        order.setStatus("待确认");//订单状态
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", productId);
        Product product = productService.getOne(queryWrapper);
        order.setSellerId(product.getUserId());//卖家id
        order.setCreateTime(LocalDateTime.now());//创建时间

        if (Objects.equals(userId, product.getUserId())){
            return Result.error("您不能购买自己的商品");
        }

        if(product.getStatus().equals("在售")){
            orderService.save(order);
            product.setStatus("已售");
            productService.updateById(product);//更新商品
            return Result.success("订单创建成功");
        }
        else{
            return Result.error("商品已售罄");
        }
    }

    //确认收货
    @PostMapping("/confirm")
    public Result<String> confirmOrder(@RequestParam("orderId")Long orderId, HttpServletRequest request){
        if (orderId == null) {
            return Result.error("订单ID不能为空");
        }
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.getById(orderId);//根据订单ID查询订单
        if(order == null){
            return Result.error("订单不存在");
        }
        if(!order.getBuyerId().equals(userId)){
            return Result.error("您不是该订单的买家");
        }
        order.setStatus("已完成");//订单状态
        orderService.updateById(order);//更新订单
        return Result.success("订单确认成功");
    }

    //取消订单
    @PostMapping("/cancel")
    public Result<String> cancelOrder(@RequestParam("orderId")Long orderId, HttpServletRequest request){
        if (orderId == null) {
            return Result.error("订单ID不能为空");
        }
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.getById(orderId);//根据订单ID查询订单
        if(order == null){
            return Result.error("订单不存在");
        }
        if(!order.getBuyerId().equals(userId)){
            return Result.error("您不是该订单的买家");
        }
        order.setStatus("已取消");//订单状态
        orderService.updateById(order);//更新订单
        return Result.success("订单取消成功");
    }

    //我买到的订单
    @PostMapping("/myBuy")
    public Result<List<Order>> myBuyOrder(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buyer_id", userId);
        return Result.success(orderService.list(queryWrapper));
    }

    //我卖出的订单
    @PostMapping("/mySell")
    public Result<List<Order>> mySellOrder(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_id", userId);
        return Result.success(orderService.list(queryWrapper));
    }

}














