package com.shop.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.async.JmsProducer;
import com.shop.bean.CommodityBean;
import com.shop.bean.UserBean;
import com.shop.dao.mapperDao.AdminMapper;
import com.shop.dao.mapperDao.CommodityMapper;
import com.shop.dao.mapperDao.UserMapper;
import com.shop.evt.*;
import com.shop.exceptions.AuditCommException;
import com.shop.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Autowired
    private JmsProducer jmsProducer;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 商品审核
     */
    @Transactional(rollbackFor = Exception.class)
    public ServiceRespModel auditComm(HttpServletRequest request, AuditCommEvt evt) {
        //校验用户权限
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (userBean.getUserRoot() != 1) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        //校验入参合法性
        if (StringUtils.isBlank(evt.getCommNo())) {
            return new ServiceRespModel(-1, "商品编码不能为空", null);
        }
        if (evt.getAuditStatus() == null) {
            return new ServiceRespModel(-1, "审核状态不能为空", null);
        }
        if (evt.getAuditStatus() == 2 && StringUtils.isBlank(evt.getAuditMsg())) {
            return new ServiceRespModel(-1, "审核留言不能为空", null);
        }
        //检验商品是否存在
        CommodityBean commodityBean = commodityMapper.queryCommByNo(evt.getCommNo());
        if (commodityBean == null) {
            return new ServiceRespModel(-1, "商品不存在", null);
        }
        //查询卖家
        UserBean seller = userMapper.queryUserByNo(commodityBean.getCreateUser());
        try {
            //更新用户不合格商品数
            if (evt.getAuditStatus() == 2) {
                int info1 = adminMapper.updateUserUnquaComm(commodityBean.getCreateUser());
                if (info1 != 1) {
                    throw new AuditCommException("更新用户不合格商品数失败");
                }
                SendEmailModel model = new SendEmailModel();
                model.setEmail(seller.getUserEmail());
                model.setMsg(String.format("您发布的商品 %s 审核未通过，商品编码为 %s ，审核未通过原因：%s", commodityBean.getCommName(), evt.getCommNo(), evt.getAuditMsg()));
                String json = JSON.toJSONString(model);
                jmsProducer.sendMsg("mail.send", json);
            }
            //修改商品审核状态
            int info = adminMapper.auditComm(evt, (String) request.getAttribute("userName"));
            if (info != 1) {
                throw new AuditCommException("商品审核失败");
            }
        } catch (AuditCommException e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ServiceRespModel(-1, e.getMessage(), null);
        }
        return new ServiceRespModel(1, "商品审核成功", null);
    }

    /**
     * 查看全部商品
     */
    public ServiceRespModel commList(HttpServletRequest request, PageEvt evt) {
        //校验用户权限
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (userBean.getUserRoot() != 1) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        //返回全部商品列表
        Page<CommodityBean> page = new Page<>(evt.getCurrent(), evt.getSize());
        Page<CommodityBean> commodityBeanPage = adminMapper.commList(page);
        List<CommModel> commModelList = queryCommPic(commodityBeanPage.getRecords());
        PageModel pageModel = new PageModel(commModelList, commodityBeanPage.getCurrent(), commodityBeanPage.getPages());
        return new ServiceRespModel(1, "全部商品列表", pageModel);
    }

    /**
     * 设置用户封禁状态
     */
    public ServiceRespModel setUserIsBan(HttpServletRequest request, SetUserIsBanEvt evt) {
        //校验入参合法性
        if (StringUtils.isBlank(evt.getUserNo())) {
            return new ServiceRespModel(-1, "用户编码不能为空", null);
        }
        if (evt.getIsBan() == null) {
            return new ServiceRespModel(-1, "封禁状态不能为空", null);
        }
        //校验用户权限
        UserBean admin = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (admin == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (admin.getUserRoot() != 1) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        //校验被操作用户是否存在
        UserBean user = userMapper.queryUserByNo(evt.getUserNo());
        if (user == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        //发送邮件
        if (evt.getIsBan() == 1) {
            SendEmailModel model = new SendEmailModel();
            model.setEmail(user.getUserEmail());
            model.setMsg(String.format("您的账号因多次发布不合格商品已被封禁，您将不能发布商品与购买商品"));
            String json = JSON.toJSONString(model);
            jmsProducer.sendMsg("mail.send", json);
        }
        //更新封禁状态
        UpdateUserModel model = new UpdateUserModel();
        model.setUserNo(evt.getUserNo());
        model.setIsBan(evt.getIsBan());
        int info = userMapper.updateUser(model);
        if (info == 1) {
            return new ServiceRespModel(1, "设置用户封禁状态成功", null);
        }
        return new ServiceRespModel(-1, "设置用户封禁状态失败", null);

    }

    /**
     * 全部用户列表
     */
    public ServiceRespModel userList(HttpServletRequest request, PageEvt evt) {
        //校验用户权限
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (userBean.getUserRoot() != 1) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        Page<UserBean> page = new Page<>(evt.getCurrent(), evt.getSize());
        Page<UserBean> userBeanPage = adminMapper.userList(page);
        PageModel pageModel = new PageModel(userBeanPage.getRecords(), userBeanPage.getCurrent(), userBeanPage.getPages());
        return new ServiceRespModel(1, "全部用户列表", pageModel);
    }

    /**
     * 用户认证信息审核
     */
    public ServiceRespModel auditUserAuthentication(HttpServletRequest request, AuditAuthenticationEvt evt) {
        //校验用户权限
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (userBean.getUserRoot() != 1) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        //校验用户是否存在
        UserBean user = userMapper.queryUserByNo(evt.getUserNo());
        if (user == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        //更新审核信息
        UpdateUserModel model = new UpdateUserModel();
        model.setUserNo(evt.getUserNo());
        model.setAuthentication(evt.getAuthentication());
        int info = userMapper.updateUser(model);
        if (info == 1) {
            //发送邮件
            if (evt.getAuthentication() == 2) {
                SendEmailModel sendEmailModel = new SendEmailModel();
                sendEmailModel.setEmail(user.getUserEmail());
                sendEmailModel.setMsg(String.format("您好%s，您的用户认证已通过", user.getUserName()));
                String json = JSON.toJSONString(sendEmailModel);
                jmsProducer.sendMsg("mail.send", json);
            }
            if (evt.getAuthentication() == 3) {
                SendEmailModel sendEmailModel = new SendEmailModel();
                sendEmailModel.setEmail(user.getUserEmail());
                sendEmailModel.setMsg(String.format("您好%s，您的用户认证未能通过", user.getUserName()));
                String json = JSON.toJSONString(sendEmailModel);
                jmsProducer.sendMsg("mail.send", json);
            }
            return new ServiceRespModel(1, "设置用户认证状态成功", null);
        }
        return new ServiceRespModel(-1, "设置用户认证状态失败", null);
    }

    /**
     * 设置商品推荐
     */
    public ServiceRespModel setCommRec(HttpServletRequest request, SetCommRecEvt evt) {
        //校验用户权限
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (userBean.getUserRoot() != 1) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        //校验商品是否存在
        CommodityBean comm = commodityMapper.queryCommByNo(evt.getCommNo());
        if (comm == null)
            return new ServiceRespModel(-1, "商品不存在", null);
        //设置商品推荐
        int info = adminMapper.setCommRec(evt);
        if (info == 1) {
            return new ServiceRespModel(1, "设置商品推荐成功", null);
        }
        return new ServiceRespModel(-1, "设置商品推荐失败", null);
    }

    //查询商品对应图片
    private List<CommModel> queryCommPic(List<CommodityBean> commodityBeanList) {
        List<CommModel> commModelList = new ArrayList<>();
        for (CommodityBean commodityBean : commodityBeanList) {
            CommModel model = new CommModel();
            model.setCommPicList(commodityMapper.queryPicByCommNo(commodityBean.getCommNo()));
            model.setCommodity(commodityBean);
            commModelList.add(model);
        }
        return commModelList;
    }
}
