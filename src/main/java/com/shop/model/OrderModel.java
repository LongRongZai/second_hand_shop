package com.shop.model;

import com.shop.bean.OrderBean;
import lombok.Data;

@Data
public class OrderModel {
    //订单
    private OrderBean order;
    //商品模型
    private CommModel commModel;
}
