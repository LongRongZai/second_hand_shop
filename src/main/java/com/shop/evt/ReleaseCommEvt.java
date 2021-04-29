package com.shop.evt;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReleaseCommEvt {

    //商品名称
    @ApiModelProperty(value = "商品名称", required = true)
    private String commName;
    //商品标签
    @ApiModelProperty(value = "商品标签(0 衣物,1 数码,2 食品,3 图书,4 化妆品,5 文具,6 居家)", required = true, example = "0")
    private Integer commTag;
    //商品描述
    @ApiModelProperty(value = "商品描述(限制长度150)", required = true)
    private String commDesc;
    //商品价格
    @ApiModelProperty(value = "商品价格", required = true, example = "0")
    private Double commPrice;
    //商品库存
    @ApiModelProperty(value = "商品库存", required = true, example = "0")
    private Integer commStock;

}
