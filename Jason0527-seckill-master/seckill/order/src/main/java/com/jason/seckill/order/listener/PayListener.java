package com.jason.seckill.order.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jason.seckill.order.service.PayService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayListener implements ChannelAwareMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(PayListener.class);

    @Autowired
    private PayService payService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        Long tag = message.getMessageProperties().getDeliveryTag();
        try {
            String str = new String(message.getBody(), "utf-8");
            logger.info("接收到的支付消息：{}",str);
            JSONObject json = JSON.parseObject(str);
            String orderId = json.getString("id");
            payService.confirmPay(orderId);
            channel.basicAck(tag, true);
        }catch(Exception e){
            logger.info("支付消息消费出错：{}",e.getMessage());
            logger.info("出错的tag:{}",tag);
        }
    }
}
