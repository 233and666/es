package com.example.esspringboot.controller;

import com.example.esspringboot.entity.Product;
import com.example.esspringboot.service.IProductService;
import com.example.esspringboot.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
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
                filename = filename.replaceAll("[^a-zA-Z0-9_\u4e00-\u9fa5-]", "");

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

}
