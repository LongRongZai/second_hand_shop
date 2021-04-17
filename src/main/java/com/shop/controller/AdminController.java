package com.shop.controller;

import com.shop.evt.AuditCommEvt;
import com.shop.evt.SetUserIsBanEvt;
import com.shop.model.ServiceRespModel;
import com.shop.service.AdminService;
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
@RequestMapping("/admin")
@Api(tags = "管理员相关接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 商品审核
     */
    @ApiOperation("商品审核接口")
    @RequestMapping(value = "/auditComm", method = RequestMethod.POST)
    public ServiceRespModel auditComm(HttpServletRequest request, AuditCommEvt evt) {
        try {
            return adminService.auditComm(request, evt);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("商品审核功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 查看全部商品
     */
    @ApiOperation("查看全部商品接口")
    @RequestMapping(value = "/commList", method = RequestMethod.GET)
    public ServiceRespModel commList(HttpServletRequest request) {
        try {
            return adminService.commList(request);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查看全部商品功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 设置用户封禁状态
     */
    @ApiOperation("设置用户封禁状态接口")
    @RequestMapping(value = "/setUserIsBan", method = RequestMethod.POST)
    public ServiceRespModel setUserIsBan(HttpServletRequest request, SetUserIsBanEvt evt) {
        try {
            return adminService.setUserIsBan(request, evt);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("设置用户封禁状态功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 查看全部用户
     */
    @ApiOperation("查看全部用户接口")
    @RequestMapping(value = "/userList", method = RequestMethod.POST)
    public ServiceRespModel userList(HttpServletRequest request) {
        try {
            return adminService.userList(request);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查看全部用户功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }


}
