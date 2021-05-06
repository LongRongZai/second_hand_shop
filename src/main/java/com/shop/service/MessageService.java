package com.shop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shop.async.JmsProducer;
import com.shop.dao.mapperDao.UserMapper;
import com.shop.exceptions.SendMailException;
import com.shop.model.SendEmailModel;
import com.shop.model.ServiceRespModel;
import com.shop.model.VerificationCodeModel;
import com.shop.utils.Md5Util;
import com.shop.utils.VerificationCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageService {

    @Value("${mail.fromMail.sender}")
    private String sender;// 发送者
    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private JmsProducer jmsProducer;

    private Logger logger = LoggerFactory.getLogger(getClass());

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
        SendEmailModel model = new SendEmailModel();
        model.setEmail(email);
        model.setMsg("您的验证码为：" + code + " 有效期为5分钟");
        //发送邮件
        String json = JSON.toJSONString(model);
        jmsProducer.sendMsg("mail.send", json);
        VerificationCodeModel verificationCodeModel = new VerificationCodeModel();
        String time = String.valueOf(System.currentTimeMillis() + 300000);
        String vCode = Md5Util.MD5(code + email + time);
        verificationCodeModel.setCode(vCode);
        verificationCodeModel.setTime(time);
        logger.info(String.format("注册邮件已发送至%s", email));
        return new ServiceRespModel(1, "邮件发送成功", verificationCodeModel);
    }

    /**
     * 发送邮件信息
     */
    @JmsListener(destination = "mail.send")
    public void sendEmailMsg(String json) {
        SendEmailModel model = JSONObject.parseObject(json, SendEmailModel.class);
        try {
            //生成其他信息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(model.getEmail());
            message.setSubject("福大校园二手商城");// 标题
            message.setText("【福大校园二手商城】 " + model.getMsg());// 内容
            //发送邮件
            javaMailSender.send(message);
        } catch (MailSendException e) {
            logger.error("目标邮箱 " + model.getEmail() + " 不存在，邮件发送失败");
            throw new SendMailException("目标邮箱 " + model.getEmail() + " 不存在，邮件发送失败");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发送邮件系统异常");
        }
    }
}
