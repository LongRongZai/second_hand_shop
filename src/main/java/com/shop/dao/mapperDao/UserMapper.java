package com.shop.dao.mapperDao;

import com.shop.bean.UserBean;
import com.shop.dao.provider.UserProvider;
import com.shop.model.UpdateUserModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    /**
     * 查询用户
     * 通过邮箱查询
     */
    @Select("select * from t_user where userEmail = #{item} and status = 'E'")
    UserBean queryUserByEmail(@Param("item") String userAccount);

    /**
     * 查询用户
     * 通过用户编码查询
     */
    @Select("select * from t_user where userNo = #{item} and status = 'E'")
    UserBean queryUserByNo(@Param("item") String userNo);

    /**
     * 插入用户
     */
    @Insert("insert into t_user(userNO,createTime,status,userEmail,userName,userPassword,isBan,userRoot,balance,unquaComm,authentication)values" +
            "(#{item.userNo},now(),'E',#{item.userEmail},#{item.userName},#{item.userPassword},0,0,1000,0,0)")
    Integer insertUser(@Param("item") UserBean userBean);

    /**
     * 更新用户最后一次登录时间
     */
    @Update("update t_user set lastLoginTime = now() where userEmail = #{item} and status = 'E'")
    Integer updateUserLastLoginTime(@Param("item") String userEmail);

    /**
     * 修改密码
     */
    @Update("update t_user set userPassword = #{newPassword} where userNo = #{userNo} and status = 'E'")
    Integer changePassword(@Param("newPassword") String newPassword, @Param("userNo") String userNo);

    /**
     * 邮箱验证修改密码
     */
    @Update("update t_user set userPassword = #{newPassword} where userEmail = #{userEmail} and status = 'E'")
    Integer changePasswordByEmail(@Param("newPassword") String newPassword, @Param("userEmail") String userEmail);

    /**
     * 更新用户信息
     */
    @UpdateProvider(type = UserProvider.class, method = "updateUserInfo")
    Integer updateUser(UpdateUserModel model);

    /**
     * 更新用户余额
     */
    @Update("update t_user set balance = balance + #{num} where userNo = #{userNo} and status = 'E'")
    Integer updateUserBalance(@Param("num") Integer num, @Param("userNo") String userNo);

}
