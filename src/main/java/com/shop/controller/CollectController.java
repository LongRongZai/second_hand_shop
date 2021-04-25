package com.shop.controller;

import com.shop.model.ServiceRespModel;
import com.shop.service.CollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/collect")
@Api(tags = "收藏商品相关接口")
public class CollectController {

    @Autowired
    private CollectService collectService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 添加商品
     */
    @ApiOperation("添加商品接口")
    @ApiImplicitParam(name = "commNo", value = "商品编码", required = true)
    @RequestMapping(value = "/insertComm", method = RequestMethod.POST)
    public ServiceRespModel insertComm(HttpServletRequest request, String commNo) {
        try {
            return collectService.insertComm(request, commNo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加商品功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }
}
