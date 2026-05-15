package com.example.esspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author your_name
 * @since 2026-05-09
 */
@Setter
@Getter
public class Admin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 管理员ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 角色：0普通管理员，1超级管理员
     */
    private Boolean role;

    /**
     * 状态：0禁用，1启用
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @Override
    public String toString() {
        return "Admin{" +
            "id = " + id +
            ", username = " + username +
            ", password = " + password +
            ", role = " + role +
            ", status = " + status +
            ", createTime = " + createTime +
            "}";
    }
}
