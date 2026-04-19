package com.example.esspringboot.service.impl;

import com.example.esspringboot.entity.Product;
import com.example.esspringboot.mapper.ProductMapper;
import com.example.esspringboot.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Override
    public String handleImageUpload(MultipartFile image) throws Exception {
        if (image == null || image.isEmpty()) {
            return null;
        }

        // 验证图片格式
        String contentType = image.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType) && !"image/gif".equals(contentType)) {
            throw new Exception("只支持JPEG、PNG、GIF格式的图片");
        }

        // 验证文件大小
        if (image.getSize() > 5 * 1024 * 1024) { // 5MB限制
            throw new Exception("图片大小不能超过5MB");
        }

        String originalFilename = image.getOriginalFilename();//获取文件名
        String filename = null;//文件名
        String fileExt = null;//文件扩展名
        String fileName = null;//最终文件名
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
        fileName = System.currentTimeMillis() + "_" + filename + fileExt;

        // 确定存储路径（项目根目录下的uploads/images/product）
        String projectRoot = System.getProperty("user.dir"); // 获取项目根目录
        //        项目根/uploads/images/
        String url = projectRoot + File.separator + "uploads" + File.separator + "image"  + File.separator;
        // 处理Windows路径编码问题（比如空格、中文）
        url = java.net.URLDecoder.decode(url, StandardCharsets.UTF_8);
        File dest = new File(url);
        //先创建目录
        if (!dest.exists()) {
            dest.mkdirs();
        }
        //指向最终要保存的文件（目录+唯一文件名）
        File destFile = new File(url + fileName);
        image.transferTo(destFile);//将文件上传到指定目录

        //生成存入数据库的文件访问地址（前端能直接访问的路径）
        return "/uploads/image/" + fileName;
    }

}