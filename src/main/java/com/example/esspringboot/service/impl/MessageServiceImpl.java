package com.example.esspringboot.service.impl;

import com.example.esspringboot.entity.Message;
import com.example.esspringboot.mapper.MessageMapper;
import com.example.esspringboot.service.IMessageService;
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
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

}
