package com.shop.dao.mapperDao;

import com.shop.bean.UserBean;
import com.shop.evt.AuditCommEvt;
import com.shop.evt.SetCommRecEvt;
import com.shop.model.AdminCommModel;
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
    @Update("update t_commodity set auditStatus = #{item.auditStatus}, auditor = #{auditor}, auditTime = now() where commNo = #{item.commNo} and status = 'E'")
    Integer auditComm(@Param("item") AuditCommEvt auditCommEvt, @Param("auditor") String auditor);

    /**
     * 全部商品列表
     */
    @Select("select c.*, u.userName from t_commodity c left join t_user u on c.createUser = u.userNo where c.status = 'E' and u.status = 'E'")
    List<AdminCommModel> commList();


    /**
     * 全部用户列表
     */
    @Select("select * from t_user where status = 'E'")
    List<UserBean> userList();

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
