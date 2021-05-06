package com.shop.model;

import com.shop.bean.UserBean;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminUserListModel {
    //用户
    private UserBean userBean;
    //认证图片列表
    private List<String> photoList;

    public AdminUserListModel() {
        this.photoList = new ArrayList<>();
    }
}
