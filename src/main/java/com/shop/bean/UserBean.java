package com.shop.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_user")
public class UserBean {
    //用户编码
    @Id
    @Column(name = "userNo", length = 32, nullable = false)
    private String userNo;
    //用户名称
    @Column(name = "userName", length = 128, nullable = false)
    private String userName;
    //用户邮箱
    @Column(name = "userEmail", length = 128, nullable = false)
    private String userEmail;
    //用户手机号
    @Column(name = "userPhone", length = 128)
    private String userPhone;
    //用户密码
    @Column(name = "userPassword", length = 128, nullable = false)
    private String userPassword;
    //用户简介
    @Column(name = "userInfo", length = 128)
    private String userInfo;
    //用户性别
    @Column(name = "userSex", length = 128)
    private String userSex;
    //用户真实姓名
    @Column(name = "userRealName", length = 128)
    private String userRealName;
    //头像地址
    @Column(name = "profileUrl", length = 128)
    private String profileUrl;
    //不合格商品数
    @Column(name = "unquaComm", length = 128)
    private Integer unquaComm;
    //封禁状态(0 正常, 1 封禁)
    @Column(name = "isBan", length = 128)
    private Integer isBan;
    //用户权限(0 普通用户, 1 管理员)
    @Column(name = "userRoot", length = 128)
    private Integer userRoot;
    //余额
    @Column(name = "balance", length = 128)
    private long balance;
    //认证状态(0 未认证, 1 认证中, 2 认证通过 , 3 认证失败)
    @Column(name = "authentication", length = 128)
    private Integer authentication;
    //学号
    @Column(name = "Sno", length = 128)
    private String sno;
    //学院
    @Column(name = "college", length = 128)
    private String college;
    //认证照片地址
    @Column(name = "photoUrl", length = 128)
    private String photoUrl;
    //最近登录时间
    @Column(name = "lastLoginTime", length = 128)
    private Date lastLoginTime;
    //状态
    @Column(name = "status", length = 128, nullable = false)
    private String status;
    //创建时间
    @Column(name = "createTime", length = 128)
    private Date createTime;
    //创建人
    @Column(name = "createUser", length = 128)
    private String createUser;
    //更新时间
    @Column(name = "updateTime", length = 128)
    private Date updateTime;
    //更新人员
    @Column(name = "updateUser", length = 128)
    private String updateUser;
}
