package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SubmitOrderEvt {
    //商品编码
    @ApiModelProperty(value = "商品编码", required = true)
    private String commNo;
    //收货地址
    @ApiModelProperty(value = "收货地址", required = true)
    private String address;
    //收货人
    @ApiModelProperty(value = "收货人", required = true)
    private String consignee;
    //收货人手机号
    @ApiModelProperty(value = "收货人手机号", required = true, example = "0")
    private String phone;
    //购买数量
    @ApiModelProperty(value = "购买数量", required = true, example = "1")
    private Integer num;
}
