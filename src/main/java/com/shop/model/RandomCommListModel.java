package com.shop.model;

import lombok.Data;

@Data
public class RandomCommListModel {
    //查询条数
    private Integer num;
    //是否为推荐商品(0 未推荐, 1 推荐)
    private Integer recommend;
}