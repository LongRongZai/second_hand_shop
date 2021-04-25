package com.shop.dao.mapperDao;

import com.shop.model.InsertCollectCommModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CollectMapper {

    //插入商品
    @Insert("insert into t_collect(collectNo, commNo, createUser, createTime, status, collectStatus)values" +
            "(#{model.collectNo}, #{model.commNo}, #{model.createUser}, now(), 'E', 0)")
    Integer insertComm(@Param("model")InsertCollectCommModel model);
}
