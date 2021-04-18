package com.shop.dao.provider;

import com.shop.model.UpdateUserModel;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

public class UserProvider {

    //修改个人信息
    public String updateUserInfo(final UpdateUserModel model) {
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
                if (StringUtils.isNotBlank(model.getUserRealName())) {
                    SET("userRealName = #{userRealName}");
                }
                if (StringUtils.isNotBlank(model.getCollege())) {
                    SET("college = #{college}");
                }
                if (StringUtils.isNotBlank(model.getSno())) {
                    SET("sno = #{sno}");
                }
                if (StringUtils.isNotBlank(model.getPhotoUrl())) {
                    SET("photoUrl = #{photoUrl}");
                }
                if (model.getAuthentication() != null) {
                    SET("authentication = #{authentication}");
                }
                if (model.getIsBan() != null) {
                    SET("isBan = #{isBan}");
                }

            }
        };
        return sql.toString();
    }

}
