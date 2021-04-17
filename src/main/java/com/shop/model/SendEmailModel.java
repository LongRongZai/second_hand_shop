package com.shop.model;

import lombok.Data;

@Data
public class SendEmailModel {
    //邮箱
    private String email;
    //消息
    private String msg;
}
