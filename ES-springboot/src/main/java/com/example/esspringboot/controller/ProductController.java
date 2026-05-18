package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.esspringboot.cache.ProductCacheService;
import com.example.esspringboot.entity.Product;
import com.example.esspringboot.entity.ProductSearch;
import com.example.esspringboot.service.IProductService;
import com.example.esspringboot.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private IProductService productService;
    @Autowired
    private ProductCacheService productCacheService;

    // 发布商品
    @PostMapping("/publish")
    public Result<String> publish(
            @RequestParam("title") String title,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request){

        System.out.println("标题："+title);
        System.out.println("价格："+price);
        System.out.println("分类："+category);
        System.out.println("描述："+description);
        System.out.println("图片："+image);
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        if (title==null || title.trim().isEmpty()){

            return Result.error("标题不能为空");
        }
        if (price==null||price.compareTo(BigDecimal.ZERO)<0){
            return Result.error("价格不能小于等于0或者为空");
        }
        //创建商品对象
        Product product=new Product();
        product.setTitle(title);
        product.setPrice(price);
        product.setCategory(category);
        product.setDescription(description);
        product.setStatus("在售");
        product.setUserId(userId);
        product.setCreateTime(LocalDateTime.now());
        if (image!=null && !image.isEmpty()){
            try {
                String imagePath = productService.handleImageUpload(image);
                System.out.println("图片路径：" + imagePath);
                product.setImage(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error("文件上传失败：" + e.getMessage());
            }
        }
        
        
        try {
            boolean saveSuccess=productService.save(product);
            if(saveSuccess){

                return Result.success("商品发布成功");
            }else {
                return  Result.error("发布失败");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error("服务器异常，修改失败");
        }
    }

    // 更新商品
    @PostMapping("/update/{id}")
    public Result<String> updateProduct(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") Boolean deleteImage,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        System.out.println("更新商品用户ID：======"+userId);
        System.out.println("更新商品image：======"+image);
        System.out.println("更新商品deleteImage：======"+deleteImage);
        if (userId == null) {
            return Result.error("用户未登录");
        }
        // 检查商品是否存在
        Product existingProduct = productService.getById(id);
        if (existingProduct == null) {
            return Result.error("商品不存在");
        }
        // 检查用户是否有权限修改该商品
        if (!existingProduct.getUserId().equals(userId)) {
            return Result.error("无权修改此商品");
        }
        // 更新商品信息
        existingProduct.setTitle(title);
        existingProduct.setPrice(price);
        existingProduct.setCategory(category);
        existingProduct.setDescription(description);
        
        // 处理图片上传
        if (image != null && !image.isEmpty()) {
            try {
                String imagePath = productService.handleImageUpload(image);
                existingProduct.setImage(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error("图片上传失败");
            }
        } else if (deleteImage) {
            // 删除图片
            System.out.println("删除图片路径=======："+existingProduct.getImage());
            existingProduct.setImage(null);
        }
        
        try {
            // 使用UpdateWrapper明确更新字段，确保null值也能被更新
            com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Product> wrapper = new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
            wrapper.eq("id", id)
                   .set("title", title)
                   .set("price", price)
                   .set("category", category)
                   .set("description", description)
                   .set("image", existingProduct.getImage());
                    
            boolean saveSuccess = productService.update(wrapper);
            System.out.println("更新商品=======："+existingProduct);
            if (saveSuccess) {
                // 更新缓存
                productCacheService.updateProductCache(existingProduct);
                return Result.success("商品更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常，更新失败");
        }
    }
    // 下架或上架商品
    @PostMapping("/off/{id}")
    public Result<String> offProduct(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        // 检查商品是否存在
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 检查用户是否有权限下架该商品
        if (!product.getUserId().equals(userId)) {
            return Result.error("无权操作此商品");
        }
        if(product.getStatus().equals("已售")||product.getStatus().equals("封禁")){
            return Result.error("已售或封禁商品不能进行此操作");
        }

        // 下架或上架商品
        if(product.getStatus().equals("在售")){
            product.setStatus("下架");
        }else if(product.getStatus().equals("下架")){
            product.setStatus("在售");
        }

        try {
            boolean saveSuccess = productService.saveOrUpdate(product);
            if (saveSuccess) {
                return Result.success("商品操作成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常，操作失败");
        }
    }

    //删除商品
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id,HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        // 检查商品是否存在
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 检查用户是否有权限删除该商品
        if (!product.getUserId().equals(userId)) {
            return Result.error("无权删除此商品");
        }
        System.out.println("准备删除商品=======："+product);
        if(product.getStatus().equals("已售")||product.getStatus().equals("封禁")){
            return Result.error("已售或封禁商品不能进行此操作");
        }

        try {
            boolean deleteSuccess = productService.removeById(id);
            if (deleteSuccess) {
                // 删除缓存
                productCacheService.deleteProductCache(id);
                return Result.success("商品删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常，删除失败");
        }
    }

    //商品详细
    @GetMapping("/detail/{id}")
    public Result<Product> detail(@PathVariable Long id){

        Product product = productCacheService.getProductById(id);//从缓存查询商品
        System.out.println("商品详细结束从缓存查询商品=======："+product);
        //Product product = productService.getById(id);//从数据库查询商品
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    //我的商品列表
    @GetMapping("/my")
    public Result<IPage<Product>> my(HttpServletRequest request,
                      @RequestParam(defaultValue = "1") int pageNum,     // 页码
                      @RequestParam(defaultValue = "10") int pageSize,   //每页数量
                      @RequestParam(defaultValue = "全部") String status
                                                                        ){
        Long userId = (Long) request.getAttribute("userId");
        //防止前端乱传数据
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        // 创建分页对象
        Page<Product> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        if (!status.equals("全部")){
            queryWrapper.eq("status",status);
        }
        queryWrapper.orderByDesc("create_time");//按时间排序
        IPage<Product> productIPage=productService.page(page,queryWrapper);
        return Result.success(productIPage,"获取我的商品列表成功");
    }
    //商品搜索
    @PostMapping("/search")
    public Result<IPage<Product>> search( @RequestBody ProductSearch productSearch) {
        try {
            Page<Product> page = new Page<>(productSearch.getPageNum(), productSearch.getPageSize());
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            //关键词
            if (productSearch.getKeyword() != null && !productSearch.getKeyword().isEmpty()) {
                queryWrapper.like(Product::getTitle, productSearch.getKeyword());
            }
            //分类
            if (productSearch.getCategory() != null && !productSearch.getCategory().isEmpty()) {
                queryWrapper.eq(Product::getCategory, productSearch.getCategory());
            }
            //状态，只查询在售商品
            queryWrapper.eq(Product::getStatus, productSearch.getStatus());

            //排序
            if ("createTime_desc".equals(productSearch.getSort())) {
                // 最新发布（按创建时间降序）
                queryWrapper.orderByDesc(Product::getCreateTime);

            } else if ("price_min".equals(productSearch.getSort())) {
                // 价格从低到高
                queryWrapper.orderByAsc(Product::getPrice);
            } else if ("price_max".equals(productSearch.getSort())) {
                // 价格从高到低
                queryWrapper.orderByDesc(Product::getPrice);
            } else {
                // 默认排序：最新发布
                queryWrapper.orderByDesc(Product::getCreateTime);
            }
            IPage<Product> productIPage = productService.page(page, queryWrapper);
            return Result.success(productIPage, "商品搜索成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常，搜索失败");
        }
    }

}