package com.shanjupay.test.rocketmq.message;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @version 1.0
 **/
@Component
@RocketMQMessageListener(topic = "my-topic-obj",consumerGroup="demo-consumer-group-obj")
public class ConsumerSimpleObj implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String jsonString = new String(body);

        System.out.println(jsonString);
        //重试次数
        int reconsumeTimes = messageExt.getReconsumeTimes();
        if(reconsumeTimes>2){
            //将此消息加入数据库，由单独的程序或人工来处理
            //....
        }
        if(1==1){
            throw new RuntimeException("处理消息失败！！");
        }

    }
}
