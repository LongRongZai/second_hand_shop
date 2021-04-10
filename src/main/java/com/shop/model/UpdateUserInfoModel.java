package com.shop.model;


import lombok.Data;

@Data
public class UpdateUserInfoModel {

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

}
