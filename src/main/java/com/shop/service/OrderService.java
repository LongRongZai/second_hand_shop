package com.shop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.shop.model.CommOrderModel;
import com.shop.model.SendEmailModel;
import com.shop.model.ServiceRespModel;
import com.shop.model.UpdateOrderModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
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
    @Transactional
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
        if (evt.getDeTimeFrom() == null && evt.getDeTimeTo() == null) {
            return new ServiceRespModel(-1, "送货时间不能为空", null);
        }
        //查询商品是否存在
        CommodityBean comm = commodityMapper.queryCommByNo(evt.getCommNo());
        if (comm == null || comm.getAuditStatus() != 1) {
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
        //校验卖家是否存在
        UserBean saleUserBean = userMapper.queryUserByNo(comm.getCreateUser());
        if (saleUserBean == null) {
            return new ServiceRespModel(-1, "卖家不存在", null);
        }
        //校验用户余额
        Double price = comm.getCommPrice() * evt.getNum();
//        if (userBean.getBalance() - price < 0) {
//            return new ServiceRespModel(-1, "用户余额不足", null);
//        }
        //提交订单

        OrderBean orderBean = null;
        try {
            orderBean = new OrderBean();
            orderBean.setOrderNo(StringUtils.replace(UUID.randomUUID().toString(), "-", ""));
            orderBean.setCommNo(evt.getCommNo());
            orderBean.setAddress(evt.getAddress());
            orderBean.setConsignee(evt.getConsignee());
            orderBean.setCreateUser((String) request.getAttribute("userNo"));
            orderBean.setNum(evt.getNum());
            orderBean.setPhone(evt.getPhone());
            orderBean.setPrice(price);
            orderBean.setDeTimeFrom(evt.getDeTimeFrom());
            orderBean.setDeTimeTo(evt.getDeTimeTo());
            int info = orderMapper.submitOrder(orderBean);
            if (info != 1) {
                throw new OrderSystemException("订单信息提交至数据库失败");
            }
//            //扣除金额与增加金额
//            int info1 = userMapper.updateUserBalance(-price, (String) request.getAttribute("userNo"));
//            if (info1 != 1) {
//                throw new UpdateUserBalanceException("扣除用户金额失败");
//            }
//            int info2 = userMapper.updateUserBalance(price, comm.getCreateUser());
//            if (info2 != 1) {
//                throw new UpdateUserBalanceException("增加用户金额失败");
//            }
            //更新商品销量与库存
            int info3 = commodityMapper.updateCommSaleAndStock(evt.getNum(), evt.getCommNo());
            if (info3 != 1) {
                throw new OrderSystemException("更新商品销量与库存失败");
            }
        } catch (OrderSystemException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return new ServiceRespModel(-1, e.getMessage(), null);
        }
        //发送邮件
        SendEmailModel model = new SendEmailModel();
        model.setEmail(saleUserBean.getUserEmail());
        model.setMsg(String.format("您的商品 %s 出售成功，商品编码为 %s ，订单编码为 %s，了解具体信息请登录商城", comm.getCommName(), comm.getCommNo(), orderBean.getOrderNo()));
        String json = JSON.toJSONString(model);
        jmsProducer.sendMsg("mail.send", json);
        //发送邮件
        SendEmailModel model1 = new SendEmailModel();
        model1.setEmail(userBean.getUserEmail());
        model1.setMsg(String.format("您的订单提交成功，订单编码为 %s ，卖家联系方式为 %s", orderBean.getOrderNo(), saleUserBean.getUserEmail()));
        String json1 = JSON.toJSONString(model1);
        jmsProducer.sendMsg("mail.send", json1);
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
    public ServiceRespModel updateOrderStatus(HttpServletRequest request, UpdateOrderStatusEvt evt) {
        // 校验入参合法性
        if (StringUtils.isBlank(evt.getOrderNo()))
            return new ServiceRespModel(-1, "订单编码不能为空", null);
        if (evt.getOrderStatus() == null)
            return new ServiceRespModel(-1, "订单状态不能为空", null);
        //更新订单状态
        OrderBean orderBean = orderMapper.queryOrderByNo(evt.getOrderNo());
        if (orderBean == null) {
            return new ServiceRespModel(-1, "订单不存在", null);
        }
        CommodityBean commodityBean = commodityMapper.queryCommByNoUnlimited(orderBean.getCommNo());
        if (commodityBean == null) {
            return new ServiceRespModel(-1, "商品不存在", null);
        }
        //下单用户
        UserBean orderUser = userMapper.queryUserByNo(orderBean.getCreateUser());
        if (orderUser == null) {
            return new ServiceRespModel(-1, "创建订单的用户不存在", null);
        }
        //创建商品用户
        UserBean commUser = userMapper.queryUserByNo(commodityBean.getCreateUser());
        if (commUser == null) {
            return new ServiceRespModel(-1, "创建商品的用户不存在", null);
        }
        switch (evt.getOrderStatus()) {
            case 1:
                //校验用户权限
                if (!request.getAttribute("userNo").equals(commodityBean.getCreateUser())) {
                    return new ServiceRespModel(-1, "无操作权限", null);
                }
                //向用户发送邮件
                SendEmailModel model = new SendEmailModel();
                model.setEmail(orderUser.getUserEmail());
                model.setMsg(String.format("您购买的商品 %s 已发货，订单编码为 %s，了解具体信息请登录商城", commodityBean.getCommName(), orderBean.getOrderNo()));
                String json = JSON.toJSONString(model);
                jmsProducer.sendMsg("mail.send", json);
                //订单自动处理
                UpdateOrderModel updateOrderModel = new UpdateOrderModel();
                updateOrderModel.setOrderNo(evt.getOrderNo());
                updateOrderModel.setBuyer(orderUser.getUserEmail());
                updateOrderModel.setSeller(commUser.getUserEmail());
                String updateOrderModelJson = JSON.toJSONString(updateOrderModel);
                //设置7天后自动处理
                jmsProducer.delaySend("order.handle", updateOrderModelJson, 1000 * 60 * 60 * 24 * 7L);
                int info = orderMapper.updateOrderStatus(evt.getOrderStatus(), evt.getOrderNo());
                if (info != 1) {
                    return new ServiceRespModel(-1, "更新订单状态失败", null);
                }
                break;
            case 2:
                //校验用户权限
                if (!request.getAttribute("userNo").equals(orderBean.getCreateUser())) {
                    return new ServiceRespModel(-1, "无操作权限", null);
                }
                int info5 = orderMapper.updateOrderStatus(evt.getOrderStatus(), evt.getOrderNo());
                if (info5 != 1) {
                    return new ServiceRespModel(-1, "更新订单状态失败", null);
                }
                break;
            case 3:
                //校验用户权限
                if (!request.getAttribute("userNo").equals(orderBean.getCreateUser())) {
                    return new ServiceRespModel(-1, "无操作权限", null);
                }
                if (orderBean.getOrderStatus() == 2) {
                    return new ServiceRespModel(-1, "订单已完成，无法申请取消", null);
                }
                //向卖家发送邮件
                SendEmailModel model1 = new SendEmailModel();
                model1.setEmail(commUser.getUserEmail());
                model1.setMsg(String.format("您有一个订单申请退款，订单编号为 %s，了解具体信息请登录商城", orderBean.getOrderNo()));
                String json1 = JSON.toJSONString(model1);
                jmsProducer.sendMsg("mail.send", json1);
                //订单自动处理
                UpdateOrderModel updateOrderModel1 = new UpdateOrderModel();
                updateOrderModel1.setOrderNo(evt.getOrderNo());
                updateOrderModel1.setBuyer(orderUser.getUserEmail());
                updateOrderModel1.setSeller(commUser.getUserEmail());
                String updateOrderModelJson1 = JSON.toJSONString(updateOrderModel1);
                //设置7天后自动处理
                jmsProducer.delaySend("order.handle", updateOrderModelJson1, 1000 * 60 * 60 * 24 * 7L);
                int info1 = orderMapper.updateOrderStatus(evt.getOrderStatus(), evt.getOrderNo());
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
//                    int info2 = userMapper.updateUserBalance(orderBean.getPrice(), (String) request.getAttribute("userNo"));
//                    if (info2 != 1) {
//                        throw new UpdateUserBalanceException("增加用户金额失败");
//                    }
//                    int info3 = userMapper.updateUserBalance(-orderBean.getPrice(), commodityBean.getCreateUser());
//                    if (info3 != 1) {
//                        throw new UpdateUserBalanceException("扣除用户金额失败");
//                    }
                int info4 = orderMapper.updateOrderStatus(evt.getOrderStatus(), evt.getOrderNo());
                if (info4 != 1) {
                    return new ServiceRespModel(-1, "更新订单状态失败", null);
                }
                //向用户发送邮件
                SendEmailModel model2 = new SendEmailModel();
                model2.setEmail(orderUser.getUserEmail());
                model2.setMsg(String.format("您的退款申请已通过，订单编号为 %s，了解具体信息请登录商城", orderBean.getOrderNo()));
                String json2 = JSON.toJSONString(model2);
                jmsProducer.sendMsg("mail.send", json2);
                break;
            default:
                return new ServiceRespModel(-1, "订单状态不合法", null);
        }
        return new ServiceRespModel(1, "更新订单状态成功", null);
    }

    //订单自动确认与取消
    @JmsListener(destination = "order.handle")
    public void autoUpdateOrder(String json) {
        try {
            UpdateOrderModel model = JSONObject.parseObject(json, UpdateOrderModel.class);
            OrderBean orderBean = orderMapper.queryOrderByNo(model.getOrderNo());
            if (orderBean == null) {
                throw new OrderSystemException("订单不存在");
            }
            switch (orderBean.getOrderStatus()) {
                case 1:
                    int info = orderMapper.updateOrderStatus(2, model.getOrderNo());
                    if (info != 1) {
                        throw new OrderSystemException("更新订单状态失败");
                    }
                    //向买家发送邮件
                    SendEmailModel model1 = new SendEmailModel();
                    model1.setEmail(model.getBuyer());
                    model1.setMsg(String.format("尊敬的用户由于您的订单 %s 长时间未进行确认收货处理，系统已自动完成确认收货", orderBean.getOrderNo()));
                    String json1 = JSON.toJSONString(model1);
                    jmsProducer.sendMsg("mail.send", json1);

                    break;
                case 3:
                    int info1 = orderMapper.updateOrderStatus(4, model.getOrderNo());
                    if (info1 != 1) {
                        throw new OrderSystemException("更新订单状态失败");

                    }
                    //向卖家发送邮件
                    SendEmailModel model2 = new SendEmailModel();
                    model2.setEmail(model.getBuyer());
                    model2.setMsg(String.format("尊敬的商家由于您的订单 %s 长时间未进行确认取消处理，系统已自动完成确认取消", orderBean.getOrderNo()));
                    String json2 = JSON.toJSONString(model2);
                    jmsProducer.sendMsg("mail.send", json2);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("订单自动确认与取消系统异常");
        }


    }

}
