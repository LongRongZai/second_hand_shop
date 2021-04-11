package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateOrderStatusEvt {
    //订单编号
    @ApiModelProperty(value = "订单编号", required = true)
    private String orderNo;
    //订单状态
    @ApiModelProperty(value = "订单状态", required = true, example = "1")
    private Integer orderStatus;
}
