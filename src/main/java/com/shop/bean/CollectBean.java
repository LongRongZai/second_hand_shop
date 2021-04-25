package com.shop.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_collect")
public class CollectBean {
    //收藏商品编码
    @Id
    @Column(name = "collectNo", length = 32, nullable = false)
    private String collectNo;
    //商品编码
    @Column(name = "commNo", length = 32, nullable = false)
    private String commNo;
    //收藏商品状态(0 正常, 1 失效)
    @Column(name = "collectStatus", length = 128, nullable = false)
    private String collectStatus;
    //状态
    @Column(name = "status", length = 128, nullable = false)
    private String status;
    //创建时间
    @Column(name = "createTime", length = 128)
    private Date createTime;
    //创建人
    @Column(name = "createUser", length = 32, nullable = false)
    private String createUser;
    //更新时间
    @Column(name = "updateTime", length = 128)
    private Date updateTime;
    //更新人员
    @Column(name = "updateUser", length = 128)
    private String updateUser;
}
