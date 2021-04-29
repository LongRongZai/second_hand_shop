package com.shop.model;

import lombok.Data;

@Data
public class UpdateOrderModel {
    //订单编号
    private String orderNo;
    //商家邮箱
    private String seller;
    //买家邮箱
    private String buyer;
}
