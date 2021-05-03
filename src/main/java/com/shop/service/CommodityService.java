package com.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.bean.CommPicBean;
import com.shop.bean.CommodityBean;
import com.shop.bean.UserBean;
import com.shop.dao.mapperDao.CommodityMapper;
import com.shop.dao.mapperDao.UserMapper;
import com.shop.evt.PageEvt;
import com.shop.evt.ReleaseCommEvt;
import com.shop.exceptions.CommReleaseException;
import com.shop.model.*;
import com.shop.utils.ImageUtil;
import com.shop.utils.UploadFileTool;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommodityService {

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private UserMapper userMapper;

    @Value("${shop.attach.save.path}")
    private String attachSavePath;

    @Value("${shop.attach.view.path}")
    private String attachViewPath;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 初始商品列表
     */
    public ServiceRespModel initialCommList(Integer num) {
        //校验入参合法性
        if (num == null) {
            return new ServiceRespModel(-1, "查询条数不能为空", null);
        }
        //查询商品
        RandomCommListModel model = new RandomCommListModel();
        model.setNum(num);
        List<CommodityBean> commodityBeanList = commodityMapper.randomCommList(model);
        //查询商品对应的图片
        List<CommModel> commModelList = queryCommPic(commodityBeanList);
        return new ServiceRespModel(1, "初始商品列表", commModelList);
    }

    /**
     * 轮播商品列表
     */
    public ServiceRespModel bannerCommList(Integer num) {
        //校验入参合法性
        if (num == null) {
            return new ServiceRespModel(-1, "查询条数不能为空", null);
        }
        //查询商品
        RandomCommListModel model = new RandomCommListModel();
        model.setNum(num);
        model.setRecommend(1);
        List<CommodityBean> commodityBeanList = commodityMapper.randomCommList(model);
        //查询商品对应的图片
        List<CommModel> commModelList = queryCommPic(commodityBeanList);
        return new ServiceRespModel(1, "轮播商品列表", commModelList);
    }

    /**
     * 发布商品
     */
    @Transactional(rollbackFor = Exception.class)
    public ServiceRespModel releaseComm(ReleaseCommEvt evt, List<MultipartFile> commPicList, HttpServletRequest request) throws Exception {
        //校验用户状态
        UserBean userBean = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        if (userBean.getIsBan() == 1) {
            return new ServiceRespModel(-1, "用户处于封禁状态", null);
        }
        if (userBean.getAuthentication() != 2) {
            return new ServiceRespModel(2, "用户认证未通过", null);
        }
        //校验入参合法性
        if (StringUtils.isBlank(evt.getCommName())) {
            return new ServiceRespModel(-1, "商品名称不能为空", null);
        }
        if (StringUtils.isBlank(evt.getCommDesc())) {
            return new ServiceRespModel(-1, "商品描述不能为空", null);
        }
        if (evt.getCommTag() == null) {
            return new ServiceRespModel(-1, "商品标签不能为空", null);
        }
        String price = evt.getCommPrice() + "";
        if (evt.getCommPrice() < 0 && (price.length() - (price + "").indexOf(".") - 1) > 2) {
            return new ServiceRespModel(-1, "非法的商品价格", null);
        }
        if (evt.getCommStock() < 0) {
            return new ServiceRespModel(-1, "非法的商品库存", null);
        }
        if (commPicList != null) {
            if (commPicList.size() > 5) {
                return new ServiceRespModel(-1, "超出限制上传附件数量", null);
            }
            for (MultipartFile file : commPicList) {
                String name = StringUtils.replace(file.getOriginalFilename(), " ", "");
                String fileType = name.substring(name.lastIndexOf(".") + 1);
                if (!ImageUtil.isImage(fileType))
                    return new ServiceRespModel(-1, "仅支持图片格式上传", null);
            }
        }

        //将商品信息存至数据库
        try {
            CommodityBean addComm = new CommodityBean();
            String commNo = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
            addComm.setCommNo(commNo);
            addComm.setCommName(evt.getCommName());
            addComm.setCommDesc(evt.getCommDesc());
            addComm.setCommPrice(evt.getCommPrice());
            addComm.setCommStock(evt.getCommStock());
            addComm.setCommTag(evt.getCommTag());
            addComm.setCommSale(0);
            addComm.setCreateUser((String) request.getAttribute("userNo"));
            addComm.setUserName(userBean.getUserName());
            if (!evt.getCustomTags().isEmpty()) {
                StringBuffer tags = new StringBuffer();
                for (String tag : evt.getCustomTags()) {
                    tags.append(tag + "_");
                }
                addComm.setCustomTags(tags.toString());
            }
            if (commPicList != null) {
                int flag = 0;
                for (MultipartFile file : commPicList) {
                    file = ImageUtil.compressFile(file, attachSavePath + file.getOriginalFilename(), 0.2f);
                    PluploadModel pluploadModel = UploadFileTool.upload(file, attachSavePath, attachViewPath);
                    CommPicBean addCommPic = new CommPicBean();
                    addCommPic.setCommPicNo(StringUtils.replace(UUID.randomUUID().toString(), "-", ""));
                    addCommPic.setCommNo(commNo);
                    addCommPic.setPictureUrl(pluploadModel.getViewPath());
                    addCommPic.setCreateUser((String) request.getAttribute("userNo"));
                    int info1 = commodityMapper.insertCommPic(addCommPic);
                    if (info1 == 1) {
                        flag++;
                    }
                }
                if (commPicList.size() != flag) {
                    throw new CommReleaseException("商品图片上传失败");
                }
            }
            int info = commodityMapper.releaseComm(addComm);
            if (info != 1) {
                throw new CommReleaseException("商品发布失败");
            }
        } catch (CommReleaseException e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ServiceRespModel(-1, e.getMessage(), null);
        }
        logger.info(String.format("用户%s发布一件商品", request.getAttribute("userEmail")));
        return new ServiceRespModel(1, "商品发布成功", null);
    }

    /**
     * 商品搜索
     */
    public ServiceRespModel searchComm(String keyName, PageEvt evt) {
        //校验入参合法性
        if (StringUtils.isBlank(keyName)) {
            return new ServiceRespModel(-1, "搜索关键字不能为空", null);
        }
        //查询商品
        Page<CommodityBean> page = new Page<>(evt.getCurrent(), evt.getSize());
        Page<CommodityBean> commodityBeanPage = commodityMapper.queryCommByName(page, keyName);
        List<CommModel> commModelList = queryCommPic(commodityBeanPage.getRecords());
        PageModel pageModel = new PageModel(commModelList, commodityBeanPage.getCurrent(), commodityBeanPage.getPages());
        return new ServiceRespModel(1, "商品列表", pageModel);
    }

    /**
     * 商品预搜索
     */
    public ServiceRespModel preSearchComm(String keyName, Integer num) {
        //校验入参合法性
        if (num == null) {
            return new ServiceRespModel(-1, "查询条数不能为空", null);
        }
        //校验入参合法性
        if (StringUtils.isBlank(keyName)) {
            return new ServiceRespModel(-1, "搜索关键字不能为空", null);
        }
        //搜索商品
        List<CommodityBean> commodityBeanList = commodityMapper.preQueryCommByName(keyName, num);
        return new ServiceRespModel(1, "商品预搜索列表", commodityBeanList);
    }

    /**
     * 查看商品
     */
    public ServiceRespModel queryCommByNo(String commNo) {
        CommModel model = new CommModel();
        //校验入参合法性
        if (StringUtils.isBlank(commNo)) {
            return new ServiceRespModel(-1, "商品编码不能为空", null);
        }
        //查询商品详情
        CommodityBean comm = commodityMapper.queryCommByNo(commNo);
        if (comm == null) {
            return new ServiceRespModel(-1, "商品不存在", null);
        }
        model.setCommodity(comm);
        //查询商品对应图片
        List<String> commPic = commodityMapper.queryPicByCommNo(comm.getCommNo());
        model.setCommPicList(commPic);
        return new ServiceRespModel(1, "商品详情", model);
    }

    /**
     * 删除商品
     */
    public ServiceRespModel deleteComm(HttpServletRequest request, String commNo) {
        //校验入参合法性
        if (StringUtils.isBlank(commNo)) {
            return new ServiceRespModel(-1, "商品编码不能为空", null);
        }
        //校验商品是否存在
        CommodityBean commodityBean = commodityMapper.queryCommByNo(commNo);
        if (commodityBean == null) {
            return new ServiceRespModel(-1, "商品不存在", null);
        }
        //校验用户权限
        if (!request.getAttribute("userNo").equals(commodityBean.getCreateUser())) {
            return new ServiceRespModel(-1, "无操作权限", null);
        }
        //删除商品
        int info = commodityMapper.deleteComm(commNo);
        if (info == 0) {
            return new ServiceRespModel(-1, "商品删除失败", null);
        }
        return new ServiceRespModel(1, "商品删除成功", null);
    }

    /**
     * 通过标签搜索商品
     */
    public ServiceRespModel queryCommByTag(Integer commTag, PageEvt evt) {
        //校验入参合法性
        if (commTag == null) {
            return new ServiceRespModel(-1, "商品标签不能为空", null);
        }
        //查询商品
        Page<CommodityBean> page = new Page<>(evt.getCurrent(), evt.getSize());
        Page<CommodityBean> commodityBeanPage = commodityMapper.queryCommByTag(page, commTag);
        List<CommModel> commModelList = queryCommPic(commodityBeanPage.getRecords());
        PageModel pageModel = new PageModel(commModelList, commodityBeanPage.getCurrent(), commodityBeanPage.getPages());
        return new ServiceRespModel(1, "商品列表", pageModel);
    }

    /**
     * 查询用户发布的商品
     */
    public ServiceRespModel queryUserComm(HttpServletRequest request) {
        List<CommodityBean> commodityBeanList = commodityMapper.queryUserComm((String) request.getAttribute("userNo"));
        List<CommModel> commModelList = queryCommPic(commodityBeanList);
        return new ServiceRespModel(1, "用户发布的商品列表", commModelList);
    }

    //查询商品对应图片
    private List<CommModel> queryCommPic(List<CommodityBean> commodityBeanList) {
        List<CommModel> commModelList = new ArrayList<>();
        for (CommodityBean commodityBean : commodityBeanList) {
            CommModel model = new CommModel();
            model.setCommPicList(commodityMapper.queryPicByCommNo(commodityBean.getCommNo()));
            model.setCommodity(commodityBean);
            commModelList.add(model);
        }
        return commModelList;
    }
}
