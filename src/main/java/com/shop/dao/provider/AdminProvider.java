package com.shop.dao.provider;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.bean.CommodityBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

public class AdminProvider {
    public String commList(@Param("commodityBeanPage") Page<CommodityBean> commodityBeanPage, @Param("auditStatus") Integer auditStatus) {
        SQL sql = new SQL() {
            {
                SELECT("*");
                FROM("t_commodity");
                WHERE("status = 'E'");
                if (auditStatus != null) {
                    WHERE("auditStatus = #{auditStatus}");
                }
            }
        };
        return sql.toString();
    }
}
