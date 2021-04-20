package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SetCommRecEvt {
    //商品编码
    @ApiModelProperty(value = "商品编码", required = true)
    private String commNo;
    //是否为推荐商品(0 未推荐, 1 推荐)
    @ApiModelProperty(value = "是否为推荐商品(0 未推荐, 1 推荐)", required = true, example = "0")
    private Integer recommend;
}
