package com.shop.service;

import com.shop.dao.mapperDao.CollectMapper;
import com.shop.model.InsertCollectCommModel;
import com.shop.model.ServiceRespModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public class CollectService {

    @Resource
    private CollectMapper collectMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ServiceRespModel insertComm(HttpServletRequest request, String commNo) {
        //校验入参合法性
        if (StringUtils.isBlank(commNo)) {
            return new ServiceRespModel(-1, "商品编码不能为空", null);
        }
        //插入商品
        InsertCollectCommModel model = new InsertCollectCommModel();
        model.setCollectNo(StringUtils.replace(UUID.randomUUID().toString(), "-", ""));
        model.setCommNo(commNo);
        model.setCreateUser((String) request.getAttribute("userNo"));
        int info = collectMapper.insertComm(model);
        if (info != 1) {
            return new ServiceRespModel(-1, "添加商品失败", null);
        }
        return new ServiceRespModel(1, "添加商品成功", null);
    }

}
