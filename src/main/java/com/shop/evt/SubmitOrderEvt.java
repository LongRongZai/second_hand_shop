package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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
    //送达时间From
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "送达时间From", required = true, example = "2021-10-10 19:00:00")
    private Date deTimeFrom;
    //送达时间To
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "送达时间To", required = true, example = "2021-10-11 19:00:00")
    private Date deTimeTo;
}
