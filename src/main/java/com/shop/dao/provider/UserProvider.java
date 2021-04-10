package com.shop.dao.provider;

import com.shop.model.UpdateUserInfoModel;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

public class UserProvider {

    //修改个人信息
    public String updateUserInfo(final UpdateUserInfoModel model) {
        SQL sql = new SQL() {
            {
                UPDATE("t_user");
                WHERE("status = 'E' and userNo = #{userNo}");
                if (StringUtils.isNotBlank(model.getProfileUrl())) {
                    SET("profileUrl = #{profileUrl}");
                }
                if (StringUtils.isNotBlank(model.getUserInfo())) {
                    SET("userInfo = #{userInfo}");
                }
                if (StringUtils.isNotBlank(model.getUserName())) {
                    SET("userName = #{userName}");
                }
                if (StringUtils.isNotBlank(model.getUserSex())) {
                    SET("userSex = #{userSex}");
                }

            }
        };
        return sql.toString();
    }
}
