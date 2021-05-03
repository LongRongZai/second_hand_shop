package com.shop.model;

import com.shop.bean.CommodityBean;
import lombok.Data;

import java.util.List;

@Data
public class CommModel {
    //商品
    private CommodityBean commodity;
    //商品图片列表
    private List<String> commPicList;

}
