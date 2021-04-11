package com.shop.dao.mapperDao;

import com.shop.bean.OrderBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderMapper {

    /**
     * 提交订单
     */
    @Insert("insert into t_order(orderNo, commNo, address, orderStatus, status, createTime, createUser, consignee, phone , num, price)values" +
            "(#{item.orderNo}, #{item.commNo}, #{item.address}, 0, 'E', now(), #{item.createUser}, #{item.consignee}, #{item.phone}, #{item.num},#{item.price})")
    Integer submitOrder(@Param("item") OrderBean orderBean);

    /**
     * 查看用户提交的订单列表
     */
    @Select("select * from t_order where status = 'E' and createUser = #{item}")
    List<OrderBean> queryUserSubmitOrderList(@Param("item") String userNo);

    /**
     * 查看用户接收的订单列表
     */
    @Select("select o.* from t_order o left join t_commodity c on o.commNo = c.commNo " +
            "where o.status = 'E' and c.status = 'E' and c.createUser = #{item}")
    List<OrderBean> queryUserReceiveOrderList(@Param("item") String userNo);

    /**
     * 更新订单状态
     */
    @Update("update t_order set orderStatus = #{orderStatus}, updateTime = now() where orderNo = #{orderNo} and status = 'E'")
    Integer updateOrderStatus(@Param("orderStatus") Integer orderStatus, @Param("orderNo") String orderNo);

    /**
     * 查看订单
     */
    @Select("select * from t_order where status = 'E' and orderNo = #{item}")
    OrderBean queryOrderByNo(@Param("item") String orderNo);

}
