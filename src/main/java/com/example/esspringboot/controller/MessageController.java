package com.example.esspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.esspringboot.entity.Message;
import com.example.esspringboot.service.IMessageService;
import com.example.esspringboot.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */
@Controller
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private IMessageService messageService;
    //发送消息
    @PostMapping("/send")
    public Result<Message> sendMessage(@RequestParam("message")String message,
                                       @RequestParam("toId")Long toId,
                                       HttpServletRequest request){
        Long fromId = Long.parseLong(request.getHeader("userId"));//获取发送者id
        Message msg = new Message();
        msg.setContent(message);
        msg.setFromId(fromId);//发送者id
        msg.setToId(toId);//对方id
        msg.setSendTime(LocalDateTime.now());//发送时间
        messageService.save(msg);
        return Result.success(msg,"消息发送成功");
    }


    // 接收消息：获取发给当前用户的所有消息
    @GetMapping("/receive")
    public Result<List<Message>> receiveMessages(@RequestParam("currentUserId") Long currentUserId) {
        // 查询 to_id = currentUserId 的所有消息，按发送时间升序
        List<Message> messages = messageService.list(new LambdaQueryWrapper<Message>()
                .eq(Message::getToId, currentUserId)
                .orderByAsc(Message::getSendTime));
        return Result.success(messages, "获取消息成功");
    }

}
