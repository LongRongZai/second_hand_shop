package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuditAuthenticationEvt {
    //用户编码
    @ApiModelProperty(value = "用户编码", required = true)
    private String userNo;
    //认证状态
    @ApiModelProperty(value = "认证状态(0 未认证, 1 认证中, 2 认证通过 , 3 认证失败)", required = true, example = "0")
    private Integer authentication;
}
