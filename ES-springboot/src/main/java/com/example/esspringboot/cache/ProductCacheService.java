package com.example.esspringboot.cache;

import com.example.esspringboot.entity.Product;
import com.example.esspringboot.service.IProductService;
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

    /**
     * 获取商品详情（带缓存）
     */
    public Product getProductById(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;

        // 先查缓存
        Product cachedProduct = (Product) redisTemplate.opsForValue().get(key);
        if (cachedProduct != null) {
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