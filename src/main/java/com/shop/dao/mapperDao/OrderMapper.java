package com.shop.dao.mapperDao;

import com.shop.bean.OrderBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderMapper {

    /**
     * 提交订单
     */
    @Insert("insert into t_order(orderNo, commNo, address, orderStatus, status, createTime, createUser, consignee, phone ,num)values" +
            "(#{item.orderNo}, #{item.commNo}, #{item.address}, 0, 'E', now(), #{item.createUser}, #{item.consignee}, #{item.phone}, #{item.num})")
    Integer submitOrder(@Param("item") OrderBean orderBean);
}
