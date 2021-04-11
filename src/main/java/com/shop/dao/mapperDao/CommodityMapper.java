package com.shop.dao.mapperDao;

import com.shop.bean.CommPicBean;
import com.shop.bean.CommodityBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommodityMapper {

    /**
     * 随机商品列表(随机选取n条数据）
     */
    @Select("select commNo,commName from t_commodity " +
            "where status = 'E' and auditStatus = 1 order by rand() limit #{item}")
    List<CommodityBean> randomCommList(@Param("item") Integer num);

    /**
     * 查询商品对应的图片
     */
    @Select("select pictureUrl from t_commPic " +
            "where status = 'E' and commNo = #{item}")
    List<String> queryPicByCommNo(@Param("item") String commNo);

    /**
     * 发布商品
     */
    @Insert("insert into t_commodity(commNo,commName,commTag,commDesc,commPrice,commSale,commStock,status,createTime,createUser,auditStatus)values" +
            "(#{item.commNo},#{item.commName},#{item.commTag},#{item.commDesc},#{item.commPrice},#{item.commSale},#{item.commStock},'E',now(),#{item.createUser},0)")
    Integer releaseComm(@Param("item") CommodityBean commodityBean);

    /**
     * 商品搜索
     */
    @Select("select commName, commNo from t_commodity where status = 'E' and auditStatus = 1 and commName like CONCAT('%',#{item},'%')")
    List<CommodityBean> queryCommByName(@Param("item") String keyName);

    /**
     * 商品预搜索
     */
    @Select("select commName from t_commodity where status = 'E' and auditStatus = 1 and commName like CONCAT('%',#{keyName},'%') limit #{num}")
    List<String> preQueryCommByName(@Param("keyName") String keyName, @Param("num") Integer num);

    /**
     * 查看商品
     */
    @Select("select * from t_commodity where status = 'E' and commNo = #{item}")
    CommodityBean queryCommByNo(@Param("item") String commNo);

    /**
     * 插入商品图片
     */
    @Insert("insert into t_commPic(commNo,commPicNo,status,createTime,createUser,pictureUrl)values" +
            "(#{item.commNo},#{item.commPicNo},'E',now(),#{item.createUser},#{item.pictureUrl})")
    Integer insertCommPic(@Param("item") CommPicBean commPicBean);

    /**
     * 删除商品
     */
    @Update("update t_commodity c left join t_commPic cp on c.commNo = cp.commNo set c.status = 'D', cp.status = 'D' where c.commNo = #{item} and c.status = 'E' and cp.status = 'E'")
    Integer deleteComm(@Param("item") String commNo);

    /**
     * 通过标签搜索商品
     */
    @Select("select commName, commNo from t_commodity where status = 'E' and auditStatus = 1 and commTag = #{item}")
    List<CommodityBean> queryCommByTag(@Param("item") Integer commTag);

    /**
     * 查看用户发布的商品
     */
    @Select("select commName, commNo, auditStatus, createTime from t_commodity where status = 'E' and createUser = #{item}")
    List<CommodityBean> queryUserComm(@Param("item") String userNo);

    /**
     * 更新商品销量
     */
    @Update("update t_commodity set commSale = commSale + #{num} where commNo = #{commNo} and status = 'E'")
    Integer updateCommSale(@Param("num") Integer num, @Param("commNo") String commNo);

}
