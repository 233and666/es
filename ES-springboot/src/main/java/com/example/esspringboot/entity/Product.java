package com.example.esspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */
@Setter
@Getter
public class Product implements Serializable {

    @Serial//这个注解,给代码增加了一道编译期的安全检查。
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private BigDecimal price;

    private String description;

    private String image;

    private String category;

    private Long userId;

    private String status;      //在售/已售/下架

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "Product{" +
            "id = " + id +
            ", title = " + title +
            ", price = " + price +
            ", description = " + description +
            ", image = " + image +
            ", category = " + category +
            ", userId = " + userId +
            ", status = " + status +
            ", createTime = " + createTime +
            "}";
    }

}
