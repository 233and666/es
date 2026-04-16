package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.esspringboot.entity.Product;
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
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService productService;
    // 发布商品
    @PostMapping("/publish")
    public Result<String> publish(
            @RequestParam("title") String title,
            @RequestParam("price") BigDecimal price,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request){

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

            String contentType = image.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType) && !"image/gif".equals(contentType)) {
                return Result.error("只支持JPEG、PNG、GIF格式的图片");
            }

            // 验证文件大小
            if (image.getSize() > 5 * 1024 * 1024) { // 5MB限制
                return Result.error("图片大小不能超过5MB");
            }

            try{
                String originalFilename = image.getOriginalFilename();//获取文件名
                String filename=null;//文件名
                String fileExt=null;//文件扩展名
                String fileName=null;//最终文件名
                if (originalFilename != null) {
                    //获取文件名（不包含扩展名）
                    filename = originalFilename.substring(0, originalFilename.lastIndexOf("."));
                }
                if (originalFilename != null) {
                    //获取文件扩展名
                    fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                //替换文件名中的特殊字符为空字符串
                // 保留：中文、字母、数字、下划线、横线；移除【】、空格、括号等
                if (filename != null) {
                    filename = filename.replaceAll("[^a-zA-Z0-9_\u4e00-\u9fa5-]", "");
                }

                //使用当前时间戳作为文件名的前缀，确保唯一性
                // fileName = LocalDateTime.now()+"-"+filename + fileExt;
                fileName=System.currentTimeMillis() + "_" + filename + fileExt;

                // 确定存储路径（项目根目录下的uploads/images/product）
                String projectRoot = System.getProperty("user.dir"); // 获取项目根目录
                //        项目根/uploads/images/
                String url = projectRoot + File.separator + "uploads" + File.separator + "image"  + File.separator;
                // 处理Windows路径编码问题（比如空格、中文）
                url = java.net.URLDecoder.decode(url, StandardCharsets.UTF_8);
                File dest=new File(url);
                //先创建目录
                if (!dest.exists()) {
                    dest.mkdirs();
                }
                //指向最终要保存的文件（目录+唯一文件名）
                File destFile = new File(url+fileName);
                image.transferTo(destFile);//将文件上传到指定目录

                //生成存入数据库的文件访问地址（前端能直接访问的路径）
                String path="/uploads/image/"+fileName;

                product.setImage(path);

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
    @PutMapping("/update/{id}")
    public Result<String> updateProduct(@PathVariable Long id, @RequestBody Product product,
                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
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
        product.setId(id);
        try {
            boolean saveSuccess=productService.saveOrUpdate(product);
            if(saveSuccess){
                return Result.success("商品更新成功");
            }else {
                return  Result.error("更新失败");
            }
        }
        catch (Exception e){
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
        // 删除商品
        try {
            boolean deleteSuccess = productService.removeById(id);
            if (deleteSuccess) {
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
        Product product = productService.getById(id);
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


}

