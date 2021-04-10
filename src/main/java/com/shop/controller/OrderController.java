package com.shop.controller;

import com.shop.evt.SubmitOrderEvt;
import com.shop.model.ServiceRespModel;
import com.shop.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
@Api(tags = "订单相关接口")
public class OrderController {

    @Autowired
    OrderService orderService;

    Logger logger = LoggerFactory.getLogger(getClass());

    @ApiOperation("提交订单接口")
    @RequestMapping(value = "/submitOrder", method = RequestMethod.POST)
    public ServiceRespModel auditComm(HttpServletRequest request, SubmitOrderEvt evt) {
        try {
            return orderService.submitOrder(request, evt);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("提交订单功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }
}
