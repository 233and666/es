package com.example.esspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author your_name
 * @since 2026-04-15
 */

@TableName("orders")
@Setter
@Getter
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Long buyerId;

    private Long sellerId;

    private String status;         //待确认/已完成/已取消

    private LocalDateTime createTime;

    // 关联商品信息（非数据库字段）
    @TableField(exist = false)
    private Product product;


    @Override
    public String toString() {
        return "Order{" +
            "id = " + id +
            ", productId = " + productId +
            ", buyerId = " + buyerId +
            ", sellerId = " + sellerId +
            ", status = " + status +
            ", createTime = " + createTime +
            ", product = " + product +
            "}";
    }
}