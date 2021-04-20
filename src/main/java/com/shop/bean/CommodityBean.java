package com.shop.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_commodity")
public class CommodityBean {
    //商品编码
    @Id
    @Column(name = "commNo", length = 32, nullable = false)
    private String commNo;
    //商品名称
    @Column(name = "commName", length = 128, nullable = false)
    private String commName;
    //商品标签(0 衣物,1 数码,2 食品,3 图书,4 化妆品,5 文具,6 居家)
    @Column(name = "commTag", length = 128)
    private Integer commTag;
    //商品描述
    @Column(name = "commDesc", length = 512)
    private String commDesc;
    //商品价格
    @Column(name = "commPrice", length = 128)
    private Integer commPrice;
    //商品销量
    @Column(name = "commSale", length = 128)
    private Integer commSale;
    //商品库存
    @Column(name = "commStock", length = 128)
    private Integer commStock;
    //推荐(0 未推荐, 1 推荐)
    @Column(name = "recommend", length = 128)
    private Integer recommend;
    //状态
    @Column(name = "status", length = 128)
    private String status;
    //创建时间
    @Column(name = "createTime", length = 128)
    private Date createTime;
    //创建人
    @Column(name = "createUser", length = 128,nullable = false)
    private String createUser;
    //更新时间
    @Column(name = "updateTime", length = 128)
    private Date updateTime;
    //更新人员
    @Column(name = "updateUser", length = 128)
    private String updateUser;
    //审核状态(1 通过,0 审核中,2 审核不通过)
    @Column(name = "auditStatus", length = 128, nullable = false)
    private Integer auditStatus;
    //审核人
    @Column(name = "auditor", length = 128)
    private String auditor;
    //审核时间
    @Column(name = "auditTime", length = 128)
    private Date auditTime;
    //审核留言
    @Column(name = "auditMsg", length = 128)
    private Date auditMsg;

}
