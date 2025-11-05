package com.cpb.notificationservice;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class SimpleConsumer {
    public static void main(String[] args) throws Exception {
        // 创建消费者实例
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ExampleConsumerGroup");
        
        // 设置NameServer地址
        consumer.setNamesrvAddr("192.168.5.10:9876"); // 根据实际情况修改为你的NameServer地址
        
        // 订阅一个或多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe("NOTIFICATION_TOPIC", "*"); // * 表示订阅所有Tag的消息
        
        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.printf("Receive new messages: %s %n", new String(msg.getBody()));
                }
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        
        // 启动消费者实例
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }
}
