package com.shop.evt;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserInfoEvt {

    //用户名称
    @ApiModelProperty(value = "用户名称")
    private String userName;
    //用户简介
    @ApiModelProperty(value = "用户简介(限制长度为40)")
    private String userInfo;
    //用户性别
    @ApiModelProperty(value = "用户性别(男,女")
    private String userSex;
    //用户头像
    @ApiModelProperty(value = "用户头像")
    private MultipartFile profile;

}
