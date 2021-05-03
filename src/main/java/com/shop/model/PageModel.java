package com.shop.model;

import lombok.Data;

@Data
public class PageModel {
    //当前页
    private Long current;
    //总页数
    private Long pages;
    //列表
    private Object obj;

    public PageModel(Object obj, Long current, Long size) {
        this.obj = obj;
        this.current = current;
        this.pages = size;
    }
}
