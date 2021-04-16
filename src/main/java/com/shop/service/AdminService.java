package com.shop.service;

import com.shop.bean.CommodityBean;
import com.shop.bean.UserBean;
import com.shop.dao.mapperDao.AdminMapper;
import com.shop.dao.mapperDao.CommodityMapper;
import com.shop.dao.mapperDao.UserMapper;
import com.shop.evt.AuditCommEvt;
import com.shop.evt.SetUserIsBanEvt;
import com.shop.exceptions.AuditCommException;
import com.shop.model.CommModel;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Resource
    UserMapper userMapper;

    @Resource
    AdminMapper adminMapper;

    @Resource
    CommodityMapper commodityMapper;

    @Autowired
    MessageService messageService;

    Logger logger = LoggerFactory.getLogger(getClass());

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
        try {
            //更新用户不合格商品数
            if (evt.getAuditStatus() == 2) {
                int info1 = adminMapper.updateUserUnquaComm(commodityBean.getCreateUser());
                if (info1 != 1) {
                    throw new AuditCommException("更新用户不合格商品数失败");
                }
                messageService.sendEmailMsg(userBean.getUserEmail(),
                        String.format("您发布的商品 %s 审核未通过，商品编码为 %s ，审核未通过原因：%s", commodityBean.getCommName(), evt.getCommNo(), evt.getAuditMsg()));
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
    public ServiceRespModel commList(HttpServletRequest request) {
        //校验用户权限
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (userBean.getUserRoot() != 1) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        //返回全部商品列表
        List<CommodityBean> commodityBeanList = adminMapper.commList();
        List<CommModel> commModelList = queryCommPic(commodityBeanList);
        return new ServiceRespModel(1, "全部商品列表", commModelList);
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
        //设置用户封禁状态
        int info = adminMapper.setUserIsBan(evt);
        if (info == 1) {
            return new ServiceRespModel(1, "设置用户封禁状态成功", null);
        }
        return new ServiceRespModel(-1, "设置用户封禁状态失败", null);

    }

    /**
     * 全部用户列表
     */
    public ServiceRespModel userList(HttpServletRequest request) {
        //校验用户权限
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (userBean.getUserRoot() != 1) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        return new ServiceRespModel(1, "全部用户列表", adminMapper.userList());
    }


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
