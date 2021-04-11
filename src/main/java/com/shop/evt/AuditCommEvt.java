package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuditCommEvt {
    //商品编码
    @ApiModelProperty(value = "商品编码", required = true)
    private String commNo;
    //审核状态
    @ApiModelProperty(value = "审核状态(0 = 审核中,1 = 通过,2 = 不通过)", required = true, example = "0")
    private Integer auditStatus;
    //审核留言
    @ApiModelProperty(value = "审核留言(审核不通过时留言不能为空)")
    private String auditMsg;
}
