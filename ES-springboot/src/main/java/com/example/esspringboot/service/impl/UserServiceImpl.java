package com.example.esspringboot.service.impl;

import com.example.esspringboot.entity.User;
import com.example.esspringboot.mapper.UserMapper;
import com.example.esspringboot.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
