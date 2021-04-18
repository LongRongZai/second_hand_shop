package com.shop.service;

import com.shop.bean.UserBean;
import com.shop.dao.mapperDao.UserMapper;
import com.shop.evt.*;
import com.shop.model.*;
import com.shop.utils.JwtUtils;
import com.shop.utils.Md5Util;
import com.shop.utils.UploadFileTool;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Value("${shop.attach.save.path}")
    private String attachSavePath;

    @Value("${shop.attach.view.path}")
    private String attachViewPath;


    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 登录
     */
    public ServiceRespModel login(UserLoginEvt evt) {
        // 校验入参合法性
        if (StringUtils.isBlank(evt.getUserEmail()))
            return new ServiceRespModel(-1, "邮箱不能为空", null);
        if (StringUtils.isBlank(evt.getUserPassword()))
            return new ServiceRespModel(-1, "密码不能为空", null);
        // 查询用户是否存在或被删除
        UserBean userBean = userMapper.queryUserByEmail(evt.getUserEmail());
        if (userBean == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        // 验证密码是否正确
        if (!(Md5Util.MD5(evt.getUserPassword()).equals(userBean.getUserPassword())))
            return new ServiceRespModel(-1, "密码错误", null);
        //生成令牌
        String token = JwtUtils.createToken(userBean);
        //更新用户最后一次登录时间
        int info = userMapper.updateUserLastLoginTime(evt.getUserEmail());
        if (info != 1) {
            logger.error(String.format("用户%s更新最后一次登录时间失败", evt.getUserEmail()));
        }
        //返回用户信息与令牌
        UserLoginModel userLoginModel = new UserLoginModel();
        userBean.setUserPassword(null);
        userLoginModel.setToken(token);
        userLoginModel.setUserBean(userBean);
        logger.info(String.format("用户%s登录成功", evt.getUserEmail()));
        return new ServiceRespModel(1, "登录成功", userLoginModel);
    }

    /**
     * 注册
     */
    public ServiceRespModel register(UserRegisterEvt evt) {
        // 检验入参合法性
        if (StringUtils.isBlank(evt.getUserName()))
            return new ServiceRespModel(-1, "昵称不能为空", null);
        if (StringUtils.isBlank(evt.getUserEmail()))
            return new ServiceRespModel(-1, "邮箱不能为空", null);
        if (StringUtils.isBlank(evt.getUserPassword()))
            return new ServiceRespModel(-1, "密码不能为空", null);
        if (StringUtils.isBlank(evt.getCode()))
            return new ServiceRespModel(-1, "验证码不能为空", null);
        if (StringUtils.isBlank(evt.getTime()))
            return new ServiceRespModel(-1, "验证码时效不能为空", null);
        if (StringUtils.isBlank(evt.getEncryptionCode()))
            return new ServiceRespModel(-1, "加密验证码不能为空", null);
        // 检验用户是否存在
        UserBean userBean = userMapper.queryUserByEmail(evt.getUserEmail());
        if (userBean != null)
            return new ServiceRespModel(-1, "用户已存在", null);
        //校验验证码
        if (!(Md5Util.MD5(evt.getCode() + evt.getUserEmail() + evt.getTime()).equals(evt.getEncryptionCode()))) {
            return new ServiceRespModel(-1, "验证码错误", null);
        }
        if (System.currentTimeMillis() - Long.parseLong(evt.getTime()) > 0) {
            return new ServiceRespModel(-1, "验证码已过期", null);
        }
        // 将用户数据保存至数据库
        UserBean addBean = new UserBean();
        addBean.setUserNo(StringUtils.replace(UUID.randomUUID().toString(), "-", ""));
        addBean.setUserName(evt.getUserName());
        addBean.setUserEmail(evt.getUserEmail());
        addBean.setUserPassword(Md5Util.MD5(evt.getUserPassword()));
        int info = userMapper.insertUser(addBean);
        if (info == 1) {
            logger.info(String.format("用户%s注册成功", evt.getUserEmail()));
            return new ServiceRespModel(1, "注册成功", null);
        }
        return new ServiceRespModel(-1, "注册失败", null);
    }

    /**
     * 查询用户信息
     */
    public ServiceRespModel queryUserInfoByNo(HttpServletRequest request) {
        // 检验用户是否存在
        UserBean userInfo = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userInfo == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        // 返回用户信息
        return new ServiceRespModel(1, "用户信息", userInfo);
    }

    /**
     * 修改密码
     */
    public ServiceRespModel changePassword(HttpServletRequest request, ChangePasswordEvt evt) {
        // 检验入参合法性
        if (StringUtils.isBlank(evt.getNewPassword()))
            return new ServiceRespModel(-1, "新密码不能为空", null);
        if (StringUtils.isBlank(evt.getOldPassword()))
            return new ServiceRespModel(-1, "原密码不能为空", null);
        // 检验用户是否存在
        UserBean userInfo = userMapper.queryUserByNo((String) request.getAttribute("userNo"));
        if (userInfo == null)
            return new ServiceRespModel(-1, "用户不存在", null);
        // 校验旧密码是否正确
        if (!Md5Util.MD5(evt.getOldPassword()).equals(userInfo.getUserPassword())) {
            return new ServiceRespModel(-1, "密码错误", null);
        }
        //更新密码
        int info = userMapper.changePassword(evt.getNewPassword(), (String) request.getAttribute("userNo"));
        if (info == 1) {
            return new ServiceRespModel(1, "密码修改成功", null);
        }
        return new ServiceRespModel(-1, "密码修改失败", null);
    }

    /**
     * 邮箱验证修改密码
     */
    public ServiceRespModel changePasswordByEmail(ChangePasswordByEmailEvt evt) {
        // 检验入参合法性
        if (StringUtils.isBlank(evt.getUserEmail()))
            return new ServiceRespModel(-1, "邮箱不能为空", null);
        if (StringUtils.isBlank(evt.getUserPassword()))
            return new ServiceRespModel(-1, "密码不能为空", null);
        if (StringUtils.isBlank(evt.getCode()))
            return new ServiceRespModel(-1, "验证码不能为空", null);
        if (StringUtils.isBlank(evt.getTime()))
            return new ServiceRespModel(-1, "验证码时效不能为空", null);
        if (StringUtils.isBlank(evt.getEncryptionCode()))
            return new ServiceRespModel(-1, "加密验证码不能为空", null);
        //校验验证码
        if (!(Md5Util.MD5(evt.getCode() + evt.getUserEmail() + evt.getTime()).equals(evt.getEncryptionCode()))) {
            return new ServiceRespModel(-1, "验证码错误", null);
        }
        if (System.currentTimeMillis() - Long.parseLong(evt.getTime()) > 0) {
            return new ServiceRespModel(-1, "验证码已过期", null);
        }
        //更新密码
        int info = userMapper.changePasswordByEmail(Md5Util.MD5(evt.getUserPassword()), evt.getUserEmail());
        if (info == 1) {
            return new ServiceRespModel(1, "邮箱验证修改密码成功", null);
        }
        return new ServiceRespModel(-1, "邮箱验证修改密码失败", null);
    }

    /**
     * 修改个人信息
     */
    public ServiceRespModel updateUserInfo(UpdateUserInfoEvt evt, MultipartFile profile, HttpServletRequest request) throws Exception {
        int flag = 0;
        UpdateUserModel model = new UpdateUserModel();
        model.setUserNo((String) request.getAttribute("userNo"));
        //更新个人信息
        if (profile != null) {
            String name = StringUtils.replace(profile.getOriginalFilename(), " ", "");
            String fileType = name.substring(name.lastIndexOf(".") + 1);
            if (!(fileType.toLowerCase().equals("jpg") || fileType.toLowerCase().equals("jpeg") || fileType.toLowerCase().equals("png")))
                return new ServiceRespModel(-1, "仅支持图片格式上传", null);
            PluploadModel pluploadModel = UploadFileTool.upload(profile, attachSavePath, attachViewPath);
            model.setProfileUrl(pluploadModel.getViewPath());
            flag++;
        }
        if (evt.getUserName() != null) {
            model.setUserName(evt.getUserName());
            flag++;
        }
        if (evt.getUserInfo() != null) {
            if (evt.getUserInfo().length() > 40) {
                return new ServiceRespModel(-1, "简介字数超出限制长度", null);
            }
            model.setUserInfo(evt.getUserInfo());
            flag++;
        }
        if (evt.getUserSex() != null) {
            model.setUserSex(evt.getUserSex());
            flag++;
        }
        if (flag == 0) {
            return new ServiceRespModel(-1, "修改信息不能为空", null);
        }
        // 将个人信息存至数据库
        int info = userMapper.updateUser(model);
        if (info == 0) {
            return new ServiceRespModel(-1, "修改信息失败", null);
        }
        return new ServiceRespModel(1, "成功修改" + info + "条信息", null);
    }

    /**
     * 更新认证信息
     */
    public ServiceRespModel updateAuthenticationInfo(UpdateAuthenticationInfoEvt evt, MultipartFile profile, HttpServletRequest request) throws Exception {
        // 检验入参合法性
        if (StringUtils.isBlank(evt.getUserRealName()))
            return new ServiceRespModel(-1, "用户真实姓名不能为空", null);
        if (StringUtils.isBlank(evt.getCollege()))
            return new ServiceRespModel(-1, "学号不能为空", null);
        if (StringUtils.isBlank(evt.getSno()))
            return new ServiceRespModel(-1, "学院不能为空", null);
        //上传图片
        String name = StringUtils.replace(profile.getOriginalFilename(), " ", "");
        String fileType = name.substring(name.lastIndexOf(".") + 1);
        if (!(fileType.toLowerCase().equals("jpg") || fileType.toLowerCase().equals("jpeg") || fileType.toLowerCase().equals("png")))
            return new ServiceRespModel(-1, "仅支持图片格式上传", null);
        PluploadModel pluploadModel = UploadFileTool.upload(profile, attachSavePath, attachViewPath);
        //将认证信息保存至数据库
        UpdateUserModel model = new UpdateUserModel();
        model.setUserNo((String) request.getAttribute("userNo"));
        model.setPhotoUrl(pluploadModel.getViewPath());
        model.setUserRealName(evt.getUserRealName());
        model.setCollege(evt.getCollege());
        model.setSno(evt.getSno());
        model.setAuthentication(1);
        int info = userMapper.updateUser(model);
        if (info == 0) {
            return new ServiceRespModel(-1, "更新认证信息失败", null);
        }
        return new ServiceRespModel(1, "更新认证信息成功", null);
    }
}
