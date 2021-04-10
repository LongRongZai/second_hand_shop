package com.shop.service;

import com.shop.bean.CommodityBean;
import com.shop.bean.OrderBean;
import com.shop.bean.UserBean;
import com.shop.dao.mapperDao.CommodityMapper;
import com.shop.dao.mapperDao.OrderMapper;
import com.shop.dao.mapperDao.UserMapper;
import com.shop.evt.SubmitOrderEvt;
import com.shop.exceptions.OrderSubmitException;
import com.shop.model.ServiceRespModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public class OrderService {

    @Resource
    OrderMapper orderMapper;

    @Resource
    CommodityMapper commodityMapper;

    @Resource
    UserMapper userMapper;

    Logger logger = LoggerFactory.getLogger(getClass());

    //提交订单
    @Transactional(rollbackFor = Exception.class)
    public ServiceRespModel submitOrder(HttpServletRequest request, SubmitOrderEvt evt) {
        // 校验入参合法性
        if (StringUtils.isBlank(evt.getAddress()))
            return new ServiceRespModel(-1, "收货地址不能为空", null);
        if (StringUtils.isBlank(evt.getConsignee()))
            return new ServiceRespModel(-1, "收货人不能为空", null);
        if (StringUtils.isBlank(evt.getCommNo()))
            return new ServiceRespModel(-1, "商品编码不能为空", null);
        if (evt.getNum() == null)
            return new ServiceRespModel(-1, "购买数量不能为空", null);
        if (evt.getPhone() == null)
            return new ServiceRespModel(-1, "收货人手机号不能为空", null);
        //查询商品是否存在
        CommodityBean comm = commodityMapper.queryCommByNo(evt.getCommNo());
        if (comm == null) {
            return new ServiceRespModel(-1, "商品不存在", null);
        }
        //校验购买数量合法性
        if (evt.getNum() <= 0 || evt.getNum() > comm.getCommStock()) {
            return new ServiceRespModel(-1, "购买数量不合法", null);
        }
        //校验用户封禁状态
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean.getIsBan() == 1) {
            return new ServiceRespModel(-1, "用户处于封禁状态", null);
        }
        //校验用户余额
        if (userBean.getBalance() - comm.getCommPrice() * evt.getNum() < 0) {
            return new ServiceRespModel(-1, "用户余额不足", null);
        }
        //提交订单
        try {
            OrderBean orderBean = new OrderBean();
            orderBean.setOrderNo(StringUtils.replace(UUID.randomUUID().toString(), "-", ""));
            orderBean.setCommNo(evt.getCommNo());
            orderBean.setAddress(evt.getAddress());
            orderBean.setConsignee(evt.getConsignee());
            orderBean.setCreateUser((String) request.getAttribute("userNo"));
            orderBean.setNum(evt.getNum());
            orderBean.setPhone(evt.getPhone());
            int info = orderMapper.submitOrder(orderBean);
            if (info != 1) {
                throw new OrderSubmitException("订单信息提交至数据库失败");
            }
            //扣除金额与增加金额
            int info1 = userMapper.updateUserBalance(comm.getCommPrice() * evt.getNum(), (String) request.getAttribute("userNo"));
            if (info1 != 1) {
                throw new OrderSubmitException("扣除用户金额失败");
            }
            int info2 = userMapper.updateUserBalance(-comm.getCommPrice() * evt.getNum(), comm.getCreateUser());
            if (info2 != 1) {
                throw new OrderSubmitException("增加用户金额失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ServiceRespModel(-1, e.getMessage(), null);
        }
        logger.info(String.format("用户%s提交了一个订单", request.getAttribute("userEmail")));
        return new ServiceRespModel(1, "订单提交成功", null);
    }
}
