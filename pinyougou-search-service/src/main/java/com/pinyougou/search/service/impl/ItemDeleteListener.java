package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Arrays;

@Component
public class ItemDeleteListener implements MessageListener {
        @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;

        try {
            Long[] object = (Long[])objectMessage.getObject();
            System.out.println("监听收到消息"+object);
            itemSearchService.deleteByGoodsIds(Arrays.asList(object));
            System.out.println("同步删除索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
