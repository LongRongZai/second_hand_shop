package com.shop.dao.mapperDao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.bean.CommodityBean;
import com.shop.bean.UserBean;
import com.shop.dao.provider.AdminProvider;
import com.shop.evt.AuditCommEvt;
import com.shop.evt.SetCommRecEvt;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AdminMapper {

    /**
     * 商品审核
     */
    @Update("update t_commodity set auditStatus = #{item.auditStatus}, auditor = #{auditor}, auditTime = now() where commNo = #{item.commNo} and status = 'E'")
    Integer auditComm(@Param("item") AuditCommEvt auditCommEvt, @Param("auditor") String auditor);

    /**
     * 全部商品列表
     */
    @SelectProvider(type = AdminProvider.class, method = "commList")
    Page<CommodityBean> commList(Page<CommodityBean> commodityBeanPage, Integer auditStatus);


    /**
     * 全部用户列表
     */
    @Select("select * from t_user where status = 'E'")
    Page<UserBean> userList(Page<UserBean> userBeanPage);

    /**
     * 更新用户不合格商品数
     */
    @Update("update t_user set unquaComm = unquaComm + 1, updateTime = now() where userNo = #{item} and status = 'E'")
    Integer updateUserUnquaComm(@Param("item") String userNo);

    /**
     * 设置商品推荐
     */
    @Update("update t_commodity set recommend = #{item.recommend}, updateTime = now() where commNo = #{item.commNo} and status = 'E'")
    Integer setCommRec(@Param("item") SetCommRecEvt evt);


}
