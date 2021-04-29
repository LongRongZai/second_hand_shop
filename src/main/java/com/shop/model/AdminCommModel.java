package com.shop.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class AdminCommModel {
    //商品编码
    private String commNo;
    //商品名称
    private String commName;
    //商品标签(0 衣物,1 数码,2 食品,3 图书,4 化妆品,5 文具,6 居家)
    private Integer commTag;
    //商品描述
    private String commDesc;
    //商品价格
    private Double commPrice;
    //商品销量
    private Integer commSale;
    //商品库存
    private Integer commStock;
    //状态
    private String status;
    //创建时间
    private Date createTime;
    //创建人
    private String createUser;
    //更新时间
    private Date updateTime;
    //更新人员
    private String updateUser;
    //审核状态(1 通过,0 审核中,2 审核不通过)
    private Integer auditStatus;
    //审核人
    private String auditor;
    //审核时间
    private Date auditTime;
    //审核留言
    private Date auditMsg;
    //用户名称
    private String userName;
    //商品图片列表
    private List<String> commPicList;
}
