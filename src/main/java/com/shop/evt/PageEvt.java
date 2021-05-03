package com.shop.evt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageEvt {

    //当前页
    @ApiModelProperty(value = "当前页", required = true, example = "1")
    private Long current;
    //每页记录条数
    @ApiModelProperty(value = "每页记录条数", required = true, example = "1")
    private Long size;
}
