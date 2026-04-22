package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.esspringboot.entity.Favorite;
import com.example.esspringboot.entity.Product;
import com.example.esspringboot.service.IFavoriteService;
import com.example.esspringboot.service.IProductService;
import com.example.esspringboot.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    @Autowired
    private IFavoriteService favoriteService;
    
    @Autowired
    private IProductService productService;
    
    //添加收藏
    @PostMapping("/add")
    public Result<String> addFavorite(@RequestParam("productId")Long productId, HttpServletRequest request){
        System.out.println("添加收藏，商品ID：========"+productId);
        Long userId = (Long) request.getAttribute("userId");
        Favorite favorite=new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteService.save(favorite);
        return Result.success("收藏成功");
    }
    //取消收藏
    @PostMapping("/cancel")
    public Result<String> cancelFavorite(@RequestParam("productId")Long productId, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<Favorite> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("product_id",productId)
                    .eq("user_id",userId);
        Favorite favorite=favoriteService.getOne(queryWrapper);
        if(favorite != null) {
            favoriteService.removeById(favorite.getId());
            return Result.success("取消收藏成功");
        } else {
            return Result.error("收藏记录不存在");
        }
    }
    //判断是否收藏了商品
    @GetMapping("/isFavorite")
    public Result<String> isFavorite(@RequestParam("productId")Long productId, HttpServletRequest request){
        System.out.println("判断是否收藏了商品，商品ID：========"+productId);
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<Favorite> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("product_id",productId)
                    .eq("user_id",userId);
        Favorite favorite=favoriteService.getOne(queryWrapper);
        if(favorite != null) {
            return Result.success("收藏成功");
        } else {
            return Result.error("快先收藏商品");
        }
    }
    //收藏列表
    @GetMapping("/list")
    public Result<List<Product>> listFavorite(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Favorite> favoritesList = favoriteService.list(queryWrapper);
        List<Product> productList = new ArrayList<>();
        for(Favorite favorite : favoritesList) {
            Product product = productService.getById(favorite.getProductId());
            productList.add(product);
        }
        System.out.println("收藏列表：========"+productList);
        return Result.success(productList);

    }




}