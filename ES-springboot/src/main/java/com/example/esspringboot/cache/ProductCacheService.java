package com.example.esspringboot.cache;

import com.example.esspringboot.entity.Product;
import com.example.esspringboot.service.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ProductCacheService {

    private static final String PRODUCT_KEY_PREFIX = "product:";// 商品缓存键前缀
    private static final long CACHE_TTL_MINUTES = 30; // 缓存30分钟

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取商品详情（带缓存）
     */
    public Product getProductById(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;

        // 先查缓存                            当使用 redisTemplate.opsForValue().get(key) 获取缓存对象时，返回的类型是 LinkedHashMap，而不是 Product 类型。
        Object cachedObj = redisTemplate.opsForValue().get(key);//Jackson 默认将 JSON 反序列化为 LinkedHashMap，而不是你期望的 Product 类型。
        if (cachedObj != null) {
            //
            // 将LinkedHashMap转换为Product对象-------
           Product cachedProduct = objectMapper.convertValue(cachedObj, Product.class);

            System.out.println("从缓存查询到商品=======："+cachedProduct);
            return cachedProduct;
        }

        // 缓存未命中，查数据库
        Product product = productService.getById(id);
        if (product != null) {
            System.out.println("缓存未命中，查数据库=======："+product);
            // 写入缓存
            redisTemplate.opsForValue().set(key, product, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        return product;
    }

    /**
     * 更新商品缓存
     */
    public void updateProductCache(Product product) {
        String key = PRODUCT_KEY_PREFIX + product.getId();
        //直接覆盖缓存中key的商品信息
        redisTemplate.opsForValue().set(key, product, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 删除商品缓存
     */
    public void deleteProductCache(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        redisTemplate.delete(key);
    }


}