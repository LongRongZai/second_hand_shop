package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateAuthenticationInfoEvt {
    //用户真实姓名
    @ApiModelProperty(value = "用户真实姓名", required = true)
    private String userRealName;
    //学号
    @ApiModelProperty(value = "学号", required = true)
    private String sno;
    //学院
    @ApiModelProperty(value = "学院", required = true)
    private String college;
}
