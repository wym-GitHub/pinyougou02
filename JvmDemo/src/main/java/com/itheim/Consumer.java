package com.itheim;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Consumer {
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
//        Queue queue = session.createQueue("test-queue");
        Topic topic = session.createTopic("test-topic");
        //创建消费者
        MessageConsumer consumer = session.createConsumer(topic);
        //7监听消息

        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage textMessage= (TextMessage) message;
                try {
                    System.out.println(textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
