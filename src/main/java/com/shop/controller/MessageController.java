package com.shop.controller;

import com.shop.anotation.PassToken;
import com.shop.model.ServiceRespModel;
import com.shop.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@Api(tags = "通知相关接口")
public class MessageController {
    @Autowired//创建对象
    private MessageService messageService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 发送邮箱注册验证码
     */
    @PassToken
    @ApiOperation("发送邮箱注册验证码接口")
    @RequestMapping(value = "/sendEmail", method = RequestMethod.GET)
    @ApiImplicitParam(name = "userEmail", value = "邮箱账号", required = true)
    public ServiceRespModel sendEmail(String userEmail) {
        try {
            return messageService.sendEmail(userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发送邮箱注册验证码功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }
}
