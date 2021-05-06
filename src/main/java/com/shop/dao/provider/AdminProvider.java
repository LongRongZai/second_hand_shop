package com.shop.dao.provider;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.bean.CommodityBean;
import org.apache.ibatis.jdbc.SQL;

public class AdminProvider {
    public String commList(final Page<CommodityBean> commodityBeanPage, Integer auditStatus) {
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
