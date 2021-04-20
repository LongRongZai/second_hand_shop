package com.shop.dao.provider;

import com.shop.model.RandomCommListModel;
import org.apache.ibatis.jdbc.SQL;

public class CommodityProvider {
    public String randomCommList(final RandomCommListModel model) {
        SQL sql = new SQL() {
            {
                SELECT("commNo,commName,commPrice");
                FROM("t_commodity");
                WHERE("status = 'E' and auditStatus = 1");
                ORDER_BY("rand()");
                LIMIT("#{num}");
                if (model.getRecommend() != null) {
                    WHERE("recommend = #{recommend}");
                }
            }
        };
        return sql.toString();
    }
}
