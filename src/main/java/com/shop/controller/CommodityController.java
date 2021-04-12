package com.shop.controller;

import com.shop.anotation.PassToken;
import com.shop.evt.ReleaseCommEvt;
import com.shop.model.ServiceRespModel;
import com.shop.service.CommodityService;
import com.shop.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/commodity")
@Api(tags = "商品相关接口")
public class CommodityController {
    @Autowired//创建对象
    MessageService messageService;

    @Autowired
    CommodityService commodityService;

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 初始商品列表
     */
    @PassToken
    @ApiOperation("初始商品列表接口")
    @ApiImplicitParam(name = "num", value = "查询条数", required = true)
    @RequestMapping(value = "/initialCommList", method = RequestMethod.GET)
    public ServiceRespModel initialCommList(Integer num) {
        try {
            return commodityService.initialCommList(num);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("初始商品列表功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 轮播商品列表
     */
    @PassToken
    @ApiOperation("轮播商品列表接口")
    @ApiImplicitParam(name = "num", value = "查询条数", required = true)
    @RequestMapping(value = "/bannerCommList", method = RequestMethod.GET)
    public ServiceRespModel bannerCommList(Integer num) {
        try {
            return commodityService.bannerCommList(num);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("轮播商品列表功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 商品发布
     */
    @ApiOperation("商品发布接口")
    @RequestMapping(value = "/releaseComm", method = RequestMethod.POST)
    public ServiceRespModel releaseComm(@ModelAttribute ReleaseCommEvt evt, List<MultipartFile> commPicList, HttpServletRequest request) {
        try {
            return commodityService.releaseComm(evt, commPicList, request);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("商品发布功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 商品搜索
     */
    @PassToken
    @ApiOperation("商品搜索接口")
    @ApiImplicitParam(name = "keyName", value = "搜索关键字", required = true)
    @RequestMapping(value = "/searchComm", method = RequestMethod.GET)
    public ServiceRespModel searchComm(String keyName) {
        try {
            return commodityService.searchComm(keyName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("商品搜索功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 商品预搜索
     */
    @PassToken
    @ApiOperation("商品预搜索接口")
    @ApiImplicitParams
            ({@ApiImplicitParam(name = "keyName", value = "搜索关键字", required = true),
                    @ApiImplicitParam(name = "num", value = "查询条数", required = true)})
    @RequestMapping(value = "/preSearchComm", method = RequestMethod.GET)
    public ServiceRespModel preSearchComm(String keyName, Integer num) {
        try {
            return commodityService.preSearchComm(keyName, num);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("商品预搜索功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 查看商品
     */
    @PassToken
    @ApiOperation("查看商品接口")
    @ApiImplicitParam(name = "commNo", value = "商品编码", required = true)
    @RequestMapping(value = "/queryCommByNo", method = RequestMethod.GET)
    public ServiceRespModel queryCommByNo(String commNo) {
        try {
            return commodityService.queryCommByNo(commNo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查看商品功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 删除商品
     */
    @ApiOperation("删除商品接口")
    @ApiImplicitParam(name = "commNo", value = "商品编码", required = true)
    @RequestMapping(value = "/deleteComm", method = RequestMethod.POST)
    public ServiceRespModel deleteComm(HttpServletRequest request, String commNo) {
        try {
            return commodityService.deleteComm(request, commNo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除商品功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 商品标签分类
     */
    @PassToken
    @ApiOperation("商品标签分类接口")
    @ApiImplicitParam(name = "commTag", value = "商品标签", required = true)
    @RequestMapping(value = "/queryCommByTag", method = RequestMethod.GET)
    public ServiceRespModel queryCommByTag(Integer commTag) {
        try {
            return commodityService.queryCommByTag(commTag);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("商品标签分类功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

    /**
     * 查看用户发布的商品接口
     */
    @ApiOperation("查看用户发布的商品接口")
    @RequestMapping(value = "/queryUserComm", method = RequestMethod.GET)
    public ServiceRespModel queryUserComm(HttpServletRequest request) {
        try {
            return commodityService.queryUserComm(request);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查看用户发布的商品功能异常");
            return new ServiceRespModel(-1, "系统异常", null);
        }
    }

}
