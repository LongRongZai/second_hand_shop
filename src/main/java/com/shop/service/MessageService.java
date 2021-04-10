package com.shop.service;

import com.shop.dao.mapperDao.UserMapper;
import com.shop.model.ServiceRespModel;
import com.shop.model.VerificationCodeModel;
import com.shop.utils.Md5Util;
import com.shop.utils.VerificationCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageService {

    @Value("${mail.fromMail.sender}")
    String sender;// 发送者
    @Resource
    JavaMailSender javaMailSender;

    @Resource
    UserMapper userMapper;

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 发送邮箱注册验证码
     */
    public ServiceRespModel sendEmail(String email) {
        //校验入参合法性
        if (StringUtils.isBlank(email)) {
            return new ServiceRespModel(-1, "邮箱不能为空", null);
        }
        //生成验证码及其他信息
        SimpleMailMessage message = new SimpleMailMessage();
        String code = VerificationCode.generateCode(4);   //随机数生成4位验证码
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject("福大校园二手商城");// 标题
        message.setText("【福大校园二手商城】您的验证码为：" + code + " 有效期为5分钟");// 内容
        //发送邮件
        try {
            javaMailSender.send(message);
            VerificationCodeModel model = new VerificationCodeModel();
            String time = String.valueOf(System.currentTimeMillis() + 300000);
            String vCode = Md5Util.MD5(code + email + time);
            model.setCode(vCode);
            model.setTime(time);
            logger.info(String.format("注册邮件已发送至%s", email));
            return new ServiceRespModel(1, "邮件发送成功", model);
        } catch (MailSendException e) {
            return new ServiceRespModel(-1, "目标邮箱不存在", null);
        }
    }
}
