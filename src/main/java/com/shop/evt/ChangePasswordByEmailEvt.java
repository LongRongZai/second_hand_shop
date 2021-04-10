package com.shop.evt;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChangePasswordByEmailEvt {

    //用户邮箱
    @ApiModelProperty(value = "用户邮箱", required = true)
    private String userEmail;
    //用户密码
    @ApiModelProperty(value = "新密码", required = true)
    private String userPassword;
    //验证码
    @ApiModelProperty(value = "验证码", required = true)
    private String code;
    //验证码时效
    @ApiModelProperty(value = "验证码时效", required = true)
    private String time;
    //加密验证码
    @ApiModelProperty(value = "加密验证码", required = true)
    private String encryptionCode;

}
