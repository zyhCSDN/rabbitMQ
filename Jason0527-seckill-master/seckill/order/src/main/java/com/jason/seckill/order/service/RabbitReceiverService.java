package com.jason.seckill.order.service;/**
 * Created by Administrator on 2019/6/21.
 */

import com.jason.seckill.order.entity.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ通用的消息接收服务
 * @Author:zyh (SteadyJack)
 **/
@Service
public class RabbitReceiverService {

    public static final Logger logger= LoggerFactory.getLogger(RabbitReceiverService.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private RabbitReceiverService rabbitReceiverService;

    @Autowired
    private Environment env;

//    @Value(value = "mail.kill.item.success.subject")
//    private String values;


    /**
     * 秒杀异步邮件通知-接收消息
     */
    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"},containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(Object info){
        try {
            logger.info("秒杀异步邮件通知-接收消息:{}",info);

            //TODO:真正的发送邮件....
            //简单文本
            //MailDto dto=new MailDto(env.getProperty("mail.kill.item.success.subject"),"这是测试内容",new String[]{info.getEmail()});
            //mailService.sendSimpleEmail(dto);

            //花哨文本
            String content = env.getProperty("mail.kill.item.success.content");
            String format = String.format( content, "Iphone 12");
//            final String content=String.format(env.getProperty("mail.kill.item.success.content"),"111","222");
            String property = env.getProperty( "mail.send.from");
            String[] ss = {property};
//            MailDto dto=new MailDto(env.getProperty("mail.kill.item.success.subject"),content,ss);
            MailDto dto=new MailDto();

            dto.setSubject( env.getProperty("mail.kill.item.success.subject") );

            dto.setContent( format );
            dto.setTos( ss );
            mailService.sendHTMLMail(dto);
//            mailService.sendSimpleEmail(dto);

        }catch (Exception e){
            logger.error("秒杀异步邮件通知-接收消息-发生异常：",e.fillInStackTrace());
        }
    }

}












