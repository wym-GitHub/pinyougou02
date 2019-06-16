package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage=(TextMessage)message;
            String text = textMessage.getText();
            long goodsId = Long.parseLong(text);
            System.out.println("接收参数"+goodsId);
            itemPageService.genItemHtml(goodsId);
            System.out.println("同步静态化页面完成");
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
