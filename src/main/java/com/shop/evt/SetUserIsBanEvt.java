package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SetUserIsBanEvt {

    //封禁状态(0 正常, 1 封禁)
    @ApiModelProperty(value = "封禁状态(0 正常, 1 封禁)", required = true, example = "0")
    private Integer isBan;
    //用户编码
    @ApiModelProperty(value = "用户编码", required = true)
    private String userNo;

}
