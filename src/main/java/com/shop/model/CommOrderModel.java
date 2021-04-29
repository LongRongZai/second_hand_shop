package com.shop.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommOrderModel {
    //订单编码
    private String orderNo;
    //商品编码
    private String commNo;
    //收货地址
    private String address;
    //收货人
    private String consignee;
    //收货人手机号
    private String phone;
    //购买数量
    private Integer num;
    //价格
    private Double price;
    //订单状态(0 待发货(未发货),1 待收货(已发货),2 完成,3 申请取消,4 订单取消)
    private Integer orderStatus;
    //创建时间
    private Date createTime;
    //创建人
    private String createUser;
    //商品名称
    private String commName;
    //商品描述
    private String commDesc;
    //商品图片列表
    private List<String> commPicList;
}
