package com.example.esspringboot.service.impl;

import com.example.esspringboot.entity.Favorite;
import com.example.esspringboot.mapper.FavoriteMapper;
import com.example.esspringboot.service.IFavoriteService;
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
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {

}
