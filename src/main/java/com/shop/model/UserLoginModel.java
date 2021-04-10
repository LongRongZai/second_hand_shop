package com.shop.model;

import com.shop.bean.UserBean;
import lombok.Data;

@Data
public class UserLoginModel {
    //令牌
    private String token;
    //用户信息
    private UserBean userBean;
}
