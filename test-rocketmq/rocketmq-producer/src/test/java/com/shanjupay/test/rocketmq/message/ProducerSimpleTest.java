package com.shanjupay.test.rocketmq.message;

import com.shanjupay.test.rocketmq.model.OrderExt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerSimpleTest {

    @Autowired
    ProducerSimple producerSimple;

    @Test
    public void testSendSyncMsg(){
        producerSimple.sendSyncMsg("my-topic","第2条同步消息");
    }

    @Test
    public void testSendASyncMsg(){
        producerSimple.sendASyncMsg("my-topic","第1条异步消息");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendMsgByJson(){
        OrderExt orderExt = new OrderExt();
        orderExt.setId("1122");
        orderExt.setMoney(893L);
        orderExt.setCreateTime(new Date());

        producerSimple.sendMsgByJson("my-topic-obj",orderExt);
    }
    @Test
    public void testSendMsgByJsonDelay(){
        OrderExt orderExt = new OrderExt();
        orderExt.setId("55555555");
        orderExt.setMoney(1111L);
        orderExt.setCreateTime(new Date());

        producerSimple.sendMsgByJsonDelay("my-topic-obj",orderExt);
    }
}
