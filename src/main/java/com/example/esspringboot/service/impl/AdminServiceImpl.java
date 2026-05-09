package com.example.esspringboot.service.impl;

import com.example.esspringboot.entity.Admin;
import com.example.esspringboot.mapper.AdminMapper;
import com.example.esspringboot.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author your_name
 * @since 2026-05-09
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

}
