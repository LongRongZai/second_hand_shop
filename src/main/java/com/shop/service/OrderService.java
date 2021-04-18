package com.shop.service;

import com.alibaba.fastjson.JSON;
import com.shop.async.JmsProducer;
import com.shop.bean.CommodityBean;
import com.shop.bean.OrderBean;
import com.shop.bean.UserBean;
import com.shop.dao.mapperDao.CommodityMapper;
import com.shop.dao.mapperDao.OrderMapper;
import com.shop.dao.mapperDao.UserMapper;
import com.shop.evt.SubmitOrderEvt;
import com.shop.evt.UpdateOrderStatusEvt;
import com.shop.exceptions.OrderSystemException;
import com.shop.exceptions.UpdateUserBalanceException;
import com.shop.model.CommOrderModel;
import com.shop.model.SendEmailModel;
import com.shop.model.ServiceRespModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private JmsProducer jmsProducer;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 提交订单
     */
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
        if (StringUtils.isBlank(evt.getPhone()))
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
        //校验用户状态
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean.getIsBan() == 1) {
            return new ServiceRespModel(-1, "用户处于封禁状态", null);
        }
        if (userBean.getAuthentication() != 2) {
            return new ServiceRespModel(2, "用户认证未通过", null);
        }
        //校验用户余额
        int price = comm.getCommPrice() * evt.getNum();
        if (userBean.getBalance() - price < 0) {
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
            orderBean.setPrice(price);
            int info = orderMapper.submitOrder(orderBean);
            if (info != 1) {
                throw new OrderSystemException("订单信息提交至数据库失败");
            }
            //扣除金额与增加金额
            int info1 = userMapper.updateUserBalance(-price, (String) request.getAttribute("userNo"));
            if (info1 != 1) {
                throw new UpdateUserBalanceException("扣除用户金额失败");
            }
            int info2 = userMapper.updateUserBalance(price, comm.getCreateUser());
            if (info2 != 1) {
                throw new UpdateUserBalanceException("增加用户金额失败");
            }
            //更新商品销量
            int info3 = commodityMapper.updateCommSale(evt.getNum(), evt.getCommNo());
            if (info3 != 1) {
                throw new OrderSystemException("更新商品销量失败");
            }
            UserBean saleUserBean = userMapper.queryUserByNo(comm.getCreateUser());
            if (saleUserBean == null) {
                return new ServiceRespModel(-1, "卖家不存在", null);
            }
            SendEmailModel model = new SendEmailModel();
            model.setEmail(saleUserBean.getUserEmail());
            model.setMsg(String.format("您的商品%s出售成功，商品编码为%s,了解具体信息请登录商城", comm.getCommName(), comm.getCommNo()));
            String json = JSON.toJSONString(model);
            jmsProducer.sendMsg("mail.send", json);
        } catch (OrderSystemException e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ServiceRespModel(-1, e.getMessage(), null);
        }
        logger.info(String.format("用户%s提交了一个订单", request.getAttribute("userEmail")));
        return new ServiceRespModel(1, "订单提交成功", null);
    }

    /**
     * 查看用户提交的订单列表
     */
    public ServiceRespModel queryUserSubmitOrderList(HttpServletRequest request) {
        List<CommOrderModel> commOrderModelList = orderMapper.queryUserSubmitOrderList((String) request.getAttribute("userNo"));
        for (CommOrderModel commOrderModel : commOrderModelList) {
            commOrderModel.setCommPicList(commodityMapper.queryPicByCommNo(commOrderModel.getCommNo()));
        }
        return new ServiceRespModel(1, "用户提交的订单列表", commOrderModelList);
    }

    /**
     * 查看用户接收的订单列表
     */
    public ServiceRespModel queryUserReceiveOrderList(HttpServletRequest request) {
        List<CommOrderModel> commOrderModelList = orderMapper.queryUserReceiveOrderList((String) request.getAttribute("userNo"));
        for (CommOrderModel commOrderModel : commOrderModelList) {
            commOrderModel.setCommPicList(commodityMapper.queryPicByCommNo(commOrderModel.getCommNo()));
        }
        return new ServiceRespModel(1, "用户接收的订单列表", commOrderModelList);
    }

    /**
     * 更新订单状态
     */
    @Transactional(rollbackFor = Exception.class)
    public ServiceRespModel updateOrderStatus(HttpServletRequest request, UpdateOrderStatusEvt evt) {
        // 校验入参合法性
        if (StringUtils.isBlank(evt.getOrderNo()))
            return new ServiceRespModel(-1, "订单编码不能为空", null);
        if (evt.getOrderStatus() == null)
            return new ServiceRespModel(-1, "订单状态不能为空", null);
        //更新订单状态
        OrderBean orderBean = orderMapper.queryOrderByNo(evt.getOrderNo());
        CommodityBean commodityBean = commodityMapper.queryCommByNo(orderBean.getCommNo());
        switch (evt.getOrderStatus()) {
            case 1:
                //校验用户权限
                if (!request.getAttribute("userNo").equals(commodityBean.getCreateUser())) {
                    return new ServiceRespModel(-1, "无操作权限", null);
                }
                int info = orderMapper.updateOrderStatus(1, evt.getOrderNo());
                if (info != 1) {
                    return new ServiceRespModel(-1, "更新订单状态失败", null);
                }
                break;
            case 2:
            case 3:
                //校验用户权限
                if (!request.getAttribute("userNo").equals(orderBean.getCreateUser())) {
                    return new ServiceRespModel(-1, "无操作权限", null);
                }
                int info1 = orderMapper.updateOrderStatus(1, evt.getOrderNo());
                if (info1 != 1) {
                    return new ServiceRespModel(-1, "更新订单状态失败", null);
                }
                break;
            case 4:
                //校验用户权限
                if (!request.getAttribute("userNo").equals(commodityBean.getCreateUser())) {
                    return new ServiceRespModel(-1, "无操作权限", null);
                }
                //扣除金额与增加金额
                try {
                    int info2 = userMapper.updateUserBalance(orderBean.getPrice(), (String) request.getAttribute("userNo"));
                    if (info2 != 1) {
                        throw new UpdateUserBalanceException("增加用户金额失败");
                    }
                    int info3 = userMapper.updateUserBalance(-orderBean.getPrice(), commodityBean.getCreateUser());
                    if (info3 != 1) {
                        throw new UpdateUserBalanceException("扣除用户金额失败");
                    }
                } catch (OrderSystemException e) {
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new ServiceRespModel(-1, e.getMessage(), null);
                }
                break;
            default:
                return new ServiceRespModel(-1, "订单状态不合法", null);
        }
        return new ServiceRespModel(1, "更新订单状态成功", null);
    }

}
