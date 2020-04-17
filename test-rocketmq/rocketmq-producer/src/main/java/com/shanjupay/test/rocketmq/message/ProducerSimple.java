package com.shanjupay.test.rocketmq.message;

import com.shanjupay.test.rocketmq.model.OrderExt;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @version 1.0
 **/
@Component
public class ProducerSimple {

    @Autowired
    RocketMQTemplate rocketMQTemplate;


    /**
     * 发送同步消息
     * @param topic 主题
     * @param msg 消息内容
     */
    public void sendSyncMsg(String topic,String msg){
        SendResult sendResult = rocketMQTemplate.syncSend(topic, msg);
        System.out.println("");
    }


    /**
     * 异步消息
     * @param topic
     * @param msg
     */
    public void sendASyncMsg(String topic,String msg){
        rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {

            //消息发送成功的回调
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult);
            }

            //消息发送失败的回调
            @Override
            public void onException(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });





    }
    //同步发送对象消息
    public void sendMsgByJson(String topic, OrderExt orderExt){
        //将对象转成json串发送
        rocketMQTemplate.convertAndSend(topic,orderExt);
    }

    //发送延迟消息
    public void sendMsgByJsonDelay(String topic, OrderExt orderExt){
        //构建消息体
        Message<OrderExt> message = MessageBuilder.withPayload(orderExt).build();
        //String destination, Message<?> message, long timeout(发送消息超时时间，毫秒), int delayLevel 延迟等级
        rocketMQTemplate.syncSend(topic,message,1000,3);

        System.out.printf("send msg : %s",orderExt);
    }

}
