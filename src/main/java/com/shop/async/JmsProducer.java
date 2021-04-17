package com.shop.async;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.io.Serializable;

@Service
public class JmsProducer {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void sendMsg(String destinationName, Object message) {
        Destination destination = new ActiveMQQueue(destinationName);
        jmsMessagingTemplate.convertAndSend(destination, message);
        logger.info("===============>>>>>发送queue消息" + message);
    }

    public void delaySend(String queueName, Object o, Long time) {
        //获取连接工厂
        ConnectionFactory connectionFactory = this.jmsMessagingTemplate.getConnectionFactory();
        try {
            //获取连接
            Connection connection = connectionFactory.createConnection();
            connection.start();
            //获取session
            Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            // 创建一个消息队列
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            ObjectMessage message = session.createObjectMessage((Serializable) o);
            //设置延迟时间
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
            //发送
            producer.send(message);
            session.commit();
            producer.close();
            session.close();
            connection.close();
            logger.info("===============>>>>>发送queue消息" + o + ",延时" + (time / 1000L / 60L) + "分钟");
        } catch (Exception e) {
            e.getMessage();
        }
    }

}