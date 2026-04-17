package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.esspringboot.entity.Favorite;
import com.example.esspringboot.service.IFavoriteService;
import com.example.esspringboot.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

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
    //添加收藏
    @PostMapping("/add")
    public Result<String> addFavorite(@RequestParam("productId")Long productId, HttpServletRequest request){
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



}
