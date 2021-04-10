package com.shop.evt;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserLoginEvt {

    //用户邮箱
    @ApiModelProperty(value = "用户邮箱", required = true)
    private String userEmail;
    //用户密码
    @ApiModelProperty(value = "用户密码", required = true)
    private String userPassword;

}
