package com.example.esspringboot.service;

import com.example.esspringboot.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */
public interface IProductService extends IService<Product> {
    
    /**
     * 处理图片上传
     * @param image 上传的图片文件
     * @return 图片在数据库中存储的路径
     * @throws Exception 上传失败时抛出异常
     */
    String handleImageUpload(org.springframework.web.multipart.MultipartFile image) throws Exception;

}