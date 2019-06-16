package cn.itcast;

import cn.itcast.demo.JmsProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-jms-producer.xml")
public class JmsTest {
    @Autowired
    private JmsProducer jmsProducer;
    @Test
    public void test1(){

        jmsProducer.sendTextMessage("黑马大数据");
    }
}
