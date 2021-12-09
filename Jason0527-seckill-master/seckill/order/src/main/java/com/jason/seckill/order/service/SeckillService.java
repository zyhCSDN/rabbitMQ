package com.jason.seckill.order.service;

import com.alibaba.fastjson.JSON;
import com.jason.seckill.order.request.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SeckillService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    /**
     *
     * (生产订单队列消息)
     * 生产发送消息
     * @param order
     */
    public void seckill(OrderRequest order){
        //设置交换机
        rabbitTemplate.setExchange(env.getProperty("order.mq.exchange.name"));
        //设置routingkey
        rabbitTemplate.setRoutingKey(env.getProperty("order.mq.routing.key"));
        //创建消息体
        Message msg = MessageBuilder.withBody(JSON.toJSONString(order).getBytes()).build();
        //发送消息
        rabbitTemplate.convertAndSend(msg);
    }
}
