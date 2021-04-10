package com.shop.dao.mapperDao;

import com.shop.bean.CommodityBean;
import com.shop.bean.UserBean;
import com.shop.evt.AuditCommEvt;
import com.shop.evt.SetUserIsBanEvt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AdminMapper {

    /**
     * 商品审核
     */
    @Update("update t_commodity set auditStatus = #{item.auditStatus}, auditor = #{item.auditor} where commNo = #{item.commNo} and status = 'E'")
    Integer auditComm(@Param("item") AuditCommEvt auditCommEvt);

    /**
     * 全部商品列表
     */
    @Select("select * from t_commodity where status = 'E'")
    List<CommodityBean> commList();

    /**
     * 设置用户封禁状态
     */
    @Update("update t_user set isBan = #{item.isBan} where userNo = #{item.userNo} and status = 'E'")
    Integer setUserIsBan(@Param("item") SetUserIsBanEvt setUserIsBanEvt);

    /**
     * 全部用户列表
     */
    @Select("select userName, userEmail, isBan, userNo, createTime, lastLoginTime from t_user where status = 'E'")
    List<UserBean> userList();

    /**
     * 更新用户不合格商品数
     */
    @Update("update t_user set unquaComm = unquaComm + 1 where userNo = #{item} and status = 'E'")
    Integer updateUserUnquaComm(@Param("item") String userNo);


}
