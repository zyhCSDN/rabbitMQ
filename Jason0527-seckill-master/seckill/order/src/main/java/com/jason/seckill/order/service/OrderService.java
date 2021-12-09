package com.jason.seckill.order.service;

import com.alibaba.fastjson.JSON;
import com.jason.seckill.order.entity.MailDto;
import com.jason.seckill.order.entity.OrderRecord;
import com.jason.seckill.order.mapper.SeckillMapper;
import com.jason.seckill.order.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 *
 * @author Administrator
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Resource
    private SeckillMapper seckillMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;


    @Autowired
    private MailService mailService;

    /**
     * 下单，操作数据库
     * @param userId
     * @param goodsId
     */
    @Transactional()
    public void order(String userId,String goodsId){
        //该商品库存-1（当库存>0时）
        int count = seckillMapper.reduceGoodsStockById(goodsId);
        //更新成功，表明抢单成功，插入下单记录，支付状态设为2-待支付
      try {
          if(count > 0){
              OrderRecord orderRecord = new OrderRecord();
              orderRecord.setId(CommonUtils.createUUID());
              orderRecord.setGoodsId(goodsId);
              orderRecord.setUserId(userId);
              orderRecord.setPayStatus(2);
              seckillMapper.insertOrderRecord(orderRecord);
              //将该订单添加到支付队列(生产支付队列消息)
              rabbitTemplate.setExchange(env.getProperty("pay.mq.exchange.name"));
              rabbitTemplate.setRoutingKey(env.getProperty("pay.mq.routing.key"));
              rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
              String json = JSON.toJSONString(orderRecord);
              Message msg = MessageBuilder.withBody(json.getBytes()).build();
              rabbitTemplate.convertAndSend(msg);
          }else {
                //库存不足，秒杀失败
              //花哨文本
              String content = env.getProperty("notice.kill.item.fail.content");
              String format = String.format( content, "Iphone 12");
//            final String content=String.format(env.getProperty("mail.kill.item.success.content"),"111","222");
              String property = env.getProperty( "mail.send.from");
              String[] ss = {property};
//            MailDto dto=new MailDto(env.getProperty("mail.kill.item.success.subject"),content,ss);
              MailDto dto=new MailDto();

              //头部
              dto.setSubject( env.getProperty("notice.kill.item.fail.subject") );

              dto.setContent( format );
              dto.setTos( ss );
              mailService.sendHTMLMail(dto);
              logger.info( "库存不足，剩余库存 count:{},用户userId:{}秒杀失败" ,count,userId );


          }
      }catch (Exception e){
          logger.info(e.getMessage());


      }

    }

}
