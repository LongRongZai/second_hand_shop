package com.shop.evt;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChangePasswordEvt {

    //原密码
    @ApiModelProperty(value = "原密码", required = true)
    private String oldPassword;
    //新密码
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;

}
