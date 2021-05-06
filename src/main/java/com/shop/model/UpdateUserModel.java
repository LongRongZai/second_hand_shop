package com.shop.model;


import lombok.Data;

@Data
public class UpdateUserModel {

    //用户编码
    private String userNo;
    //用户名称
    private String userName;
    //用户简介
    private String userInfo;
    //用户性别
    private String userSex;
    //用户头像地址
    private String profileUrl;
    //用户真实姓名
    private String userRealName;
    //学号
    private String sno;
    //学院
    private String college;
    //认证照片地址1
    private String photoUrl1;
    //认证照片地址2
    private String photoUrl2;
    //认证状态
    private Integer authentication;
    //用户封禁状态
    private Integer isBan;



}
