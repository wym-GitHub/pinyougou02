package com.itheim;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JvmDemo {

    public static void main(String[] args) throws JMSException {
        //1创建工厂
        ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
        //2获取连接
        Connection connection = connectionFactory.createConnection();
        //3启动连接
        connection.start();
        //4获取session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5创建队列对象
        Queue queue = session.createQueue("test-queue");
        Topic topic = session.createTopic("test-topic");
        //6创建消息生产者

        MessageProducer producer = session.createProducer(topic);
        //7创建消息对象
        TextMessage textMessage = session.createTextMessage("大数据五期欢迎您");
        //8发送消息
         producer.send(textMessage);
        //9关闭资源

        producer.close();
        session.close();
        connection.close();


    }
}
