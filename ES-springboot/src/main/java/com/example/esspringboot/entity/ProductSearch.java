package com.example.esspringboot.entity;

import lombok.Data;

@Data
public class ProductSearch {
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 12;

    /**
     * 搜索关键词（匹配商品标题）
     */
    private String keyword;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 商品状态：在售/已售/下架
     */
    private String status = "在售";

    /**
     * 价格区间：0-50/50-200/200-500/500+
     */
    private String priceRange;
    /**
     * 排序方式：createTime_desc（最新发布）、price_asc（价格升序）、price_desc（价格降序）
     */
    private String sort = "createTime_desc";

}
