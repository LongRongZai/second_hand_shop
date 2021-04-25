package com.shop.model;

import lombok.Data;

@Data
public class InsertCollectCommModel {
    //收藏商品编码
    private String collectNo;
    //商品编码
    private String CommNo;
    //用户编码
    private String createUser;
}
