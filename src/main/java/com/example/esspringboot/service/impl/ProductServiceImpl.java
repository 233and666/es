package com.example.esspringboot.service.impl;

import com.example.esspringboot.entity.Product;
import com.example.esspringboot.mapper.ProductMapper;
import com.example.esspringboot.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
