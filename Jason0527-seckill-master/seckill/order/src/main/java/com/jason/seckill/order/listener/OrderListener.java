package com.jason.seckill.order.listener;


import com.alibaba.fastjson.JSONObject;
import com.jason.seckill.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息监听器（消费者）
 */
@Component
public class OrderListener implements ChannelAwareMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderListener.class);

    @Autowired
    private OrderService orderService;
    /**
     * 处理接收到的消息
     * @param message 消息体
     * @param channel 通道，确认消费用
     * @throws Exception
     */
    @Override
    public void onMessage(Message message, Channel channel){
        try{
            //获取交付tag
            long tag = message.getMessageProperties().getDeliveryTag();
            String str = new String(message.getBody(),"utf-8");
            logger.info("接收到的订单消息：{}",str);
            JSONObject obj = JSONObject.parseObject(str);
            //下单，操作数据库
            orderService.order(obj.getString("userId"),obj.getString("goodsId"));
            //确认消费
            channel.basicAck(tag,true);
        }catch(Exception e){
            logger.error("消息监听确认机制发生异常：",e.fillInStackTrace());
        }

    }
}
