package com.shop.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_order")
public class OrderBean {
    //订单编码
    @Id
    @Column(name = "orderNo", length = 32, nullable = false)
    private String orderNo;
    //商品编码
    @Column(name = "commNo", length = 32, nullable = false)
    private String commNo;
    //收货地址
    @Column(name = "address", length = 256, nullable = false)
    private String address;
    //收货人
    @Column(name = "consignee", length = 128, nullable = false)
    private String consignee;
    //收货人手机号
    @Column(name = "phone", length = 128, nullable = false)
    private String phone;
    //购买数量
    @Column(name = "num", nullable = false)
    private Integer num;
    //价格
    @Column(name = "price", nullable = false)
    private Double price;
    //送达时间From
    @Column(name = "deTimeFrom", nullable = false)
    private Date deTimeFrom;
    //送达时间To
    @Column(name = "deTimeTo", nullable = false)
    private Date deTimeTo;
    //订单状态(0 待发货(未发货),1 待收货(已发货),2 完成,3 申请取消,4 订单取消)
    @Column(name = "orderStatus", nullable = false)
    private Integer orderStatus;
    //状态
    @Column(name = "status", length = 128, nullable = false)
    private String status;
    //创建时间
    @Column(name = "createTime")
    private Date createTime;
    //创建人
    @Column(name = "createUser", length = 128, nullable = false)
    private String createUser;
    //更新时间
    @Column(name = "updateTime")
    private Date updateTime;
    //更新人员
    @Column(name = "updateUser", length = 128)
    private String updateUser;


}
