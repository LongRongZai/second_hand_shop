package com.shop.dao.mapperDao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.bean.CommPicBean;
import com.shop.bean.CommodityBean;
import com.shop.dao.provider.CommodityProvider;
import com.shop.model.RandomCommListModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommodityMapper {

    /**
     * 随机商品列表(随机选取n条数据）
     */
    @SelectProvider(type = CommodityProvider.class, method = "randomCommList")
    List<CommodityBean> randomCommList(RandomCommListModel model);

    /**
     * 查询商品对应的图片
     */
    @Select("select pictureUrl from t_commPic " +
            "where status = 'E' and commNo = #{item}")
    List<String> queryPicByCommNo(@Param("item") String commNo);

    /**
     * 发布商品
     */
    @Insert("insert into t_commodity(commNo,commName,commTag,commDesc,commPrice,commSale,commStock,status,createTime,createUser,auditStatus,recommend,customTags,userName)values" +
            "(#{item.commNo},#{item.commName},#{item.commTag},#{item.commDesc},#{item.commPrice},#{item.commSale},#{item.commStock},'E',now(),#{item.createUser},0,0,#{item.customTags},#{item.userName})")
    Integer releaseComm(@Param("item") CommodityBean commodityBean);

    /**
     * 商品搜索
     */
    @Select("select * from t_commodity where status = 'E' and auditStatus = 1 and (commName like CONCAT('%',#{item},'%') or customTags like CONCAT('%',#{item},'%'))")
    Page<CommodityBean> queryCommByName(Page<CommodityBean> commodityBeanPage, @Param("item") String keyName);


    /**
     * 商品预搜索
     */
    @Select("select commName,commNo from t_commodity where status = 'E' and auditStatus = 1 and commName like CONCAT('%',#{keyName},'%') limit #{num}")
    List<CommodityBean> preQueryCommByName(@Param("keyName") String keyName, @Param("num") Integer num);

    /**
     * 查看商品
     */
    @Select("select * from t_commodity where status = 'E' and commNo = #{item}")
    CommodityBean queryCommByNo(@Param("item") String commNo);

    /**
     * 查看商品(无限制）
     */
    @Select("select * from t_commodity where commNo = #{item}")
    CommodityBean queryCommByNoUnlimited(@Param("item") String commNo);

    /**
     * 插入商品图片
     */
    @Insert("insert into t_commPic(commNo,commPicNo,status,createTime,createUser,pictureUrl)values" +
            "(#{item.commNo},#{item.commPicNo},'E',now(),#{item.createUser},#{item.pictureUrl})")
    Integer insertCommPic(@Param("item") CommPicBean commPicBean);

    /**
     * 删除商品
     */
    @Update("update t_commodity set status = 'D', updateTime = now() where commNo = #{item} and status = 'E' ")
    Integer deleteComm(@Param("item") String commNo);

    /**
     * 通过标签搜索商品
     */
    @Select("select * from t_commodity where status = 'E' and auditStatus = 1 and commTag = #{item}")
    Page<CommodityBean> queryCommByTag(Page<CommodityBean> commodityBeanPage, @Param("item") Integer commTag);

    /**
     * 查看用户发布的商品
     */
    @Select("select * from t_commodity where status = 'E' and createUser = #{item}")
    List<CommodityBean> queryUserComm(@Param("item") String userNo);

    /**
     * 更新商品销量以及库存
     */
    @Update("update t_commodity set commSale = commSale + #{num}, commStock = commStock - #{num}, updateTime = now() where commNo = #{commNo} and status = 'E'")
    Integer updateCommSaleAndStock(@Param("num") Integer num, @Param("commNo") String commNo);


}
