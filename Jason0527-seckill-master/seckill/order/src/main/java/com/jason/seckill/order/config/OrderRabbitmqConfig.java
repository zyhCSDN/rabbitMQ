package com.jason.seckill.order.config;

import com.jason.seckill.order.listener.OrderListener;
import com.jason.seckill.order.listener.PayListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 秒杀rabbitmq配置
 */
@Configuration
public class OrderRabbitmqConfig {

    private static final Logger logger = LoggerFactory.getLogger(OrderRabbitmqConfig.class);


    @Autowired
    private Environment env;

    /**
     * channel链接工厂
     */
    @Autowired
    private CachingConnectionFactory connectionFactory;

    /**
     * 监听器容器配置
     */
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 声明rabbittemplate
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(){

        //消息发送成功确认，对应application.properties中的spring.rabbitmq.publisher-confirms=true
        connectionFactory.setPublisherConfirms(true);
        //消息发送失败确认，对应application.properties中的spring.rabbitmq.publisher-returns=true
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //设置消息发送格式为json
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setMandatory(true);
        //消息发送到exchange回调 需设置：spring.rabbitmq.publisher-confirms=true
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                logger.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }
        });
        //消息从exchange发送到queue失败回调  需设置：spring.rabbitmq.publisher-returns=true
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                logger.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });
        return rabbitTemplate;
    }

    //---------------------------------------订单队列------------------------------------------------------

    /**
     * 声明订单队列的交换机
     * @return
     */
    @Bean("orderTopicExchange")
    public TopicExchange orderTopicExchange(){
        //设置为持久化 不自动删除
        return new TopicExchange(env.getProperty("order.mq.exchange.name"),true,false);
    }

    /**
     * 声明订单队列
     * @return
     */
    @Bean("orderQueue")
    public Queue orderQueue(){
        return new Queue(env.getProperty("order.mq.queue.name"),true);
    }

    /**
     * 将队列绑定到交换机
     * @return
     */
    @Bean
    public Binding simpleBinding(){
        return BindingBuilder.bind(orderQueue()).to(orderTopicExchange()).with(env.getProperty("order.mq.routing.key"));
    }

    /**
     * 注入订单对列消费监听器
     */
    @Autowired
    private OrderListener orderListener;

    /**
     * 声明订单队列监听器配置容器
     * @return
     */
    @Bean("orderListenerContainer")
    public SimpleMessageListenerContainer orderListenerContainer(){
        //创建监听器容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //将配置信息和链接信息赋给容器工厂
        factoryConfigurer.configure(factory,connectionFactory);
        //容器工厂创建监听器容器
        SimpleMessageListenerContainer container = factory.createListenerContainer();
        //指定监听器监听的队列
        container.setQueues(orderQueue());
        //指定监听器
        container.setMessageListener(orderListener);
        return container;
    }

    /**
     *
     * 每次设置都是三个：队列，交换机，通过routing.key 将队列绑定到交换机 （此项目包含，订单队列，支付队列，死信队列）
     *
     */

    //--------------------------------------------支付队列-------------------------------------------------

    /**
     * 死信队列：也叫延迟队列，可以设置队列中的消息过期时间
     * 死信交换机：死信队列声明同时声明死信交换机，死信队列中的消息到期后(延迟队列 15分钟)会通过死信交换机进入到绑定的真正队列做监听消费处理
     */



    /**
     * 死信队列，十五分钟超时
     * @return
     */
    @Bean
    public Queue payDeadLetterQueue(){
        Map args = new HashMap();
        //声明死信交换机
        args.put("x-dead-letter-exchange",env.getProperty("pay.dead-letter.mq.exchange.name"));
        //声明死信routingkey
        args.put("x-dead-letter-routing-key",env.getProperty("pay.dead-letter.mq.routing.key"));
        //声明死信队列中的消息过期时间
        args.put("x-message-ttl",env.getProperty("pay.mq.ttl",int.class));
        //创建死信队列
        return new Queue(env.getProperty("pay.dead-letter.mq.queue.name"),true,false,false,args);
    }


    /**
     * 死信交换机
     * @return
     */
    @Bean
    public TopicExchange payDeadLetterExchange(){
        return new TopicExchange(env.getProperty("pay.dead-letter.mq.exchange.name"),true,false);
    }



    /**
     * 将主交换机绑定到死信队列
     * @return
     */
    @Bean
    public Binding payBinding(){
        return BindingBuilder.bind(payDeadLetterQueue()).to(payTopicExchange()).with(env.getProperty("pay.mq.routing.key"));
    }


    /**
     * 支付队列（主队列）
     * @return
     */
    @Bean
    public Queue payQueue(){
        return new Queue(env.getProperty("pay.mq.queue.name"),true);
    }


    /**
     * 支付队列交换机（主交换机）
     * @return
     */
    @Bean
    public TopicExchange payTopicExchange(){
        return new TopicExchange(env.getProperty("pay.mq.exchange.name"),true,false);
    }


    /**
     * 将主队列绑定到死信交换机
     * @return
     */
    @Bean
    public Binding payDeadLetterBinding(){
        return BindingBuilder.bind(payQueue()).to(payDeadLetterExchange()).with(env.getProperty("pay.dead-letter.mq.routing.key"));
    }



    /**
     * 注入支付监听器
     */
    @Autowired
    private PayListener payListener;

    /**
     * 支付队列监听器容器
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer payMessageListenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);
        SimpleMessageListenerContainer listenerContainer = factory.createListenerContainer();
        listenerContainer.setMessageListener(payListener);
        listenerContainer.setQueues(payQueue());
        return listenerContainer;
    }



    //构建异步发送邮箱通知的消息模型
    @Bean
    public Queue successEmailQueue(){
        return new Queue(env.getProperty("mq.kill.item.success.email.queue"),true);
    }

    @Bean
    public TopicExchange successEmailExchange(){
        return new TopicExchange(env.getProperty("mq.kill.item.success.email.exchange"),true,false);
    }

    @Bean
    public Binding successEmailBinding(){
        return BindingBuilder.bind(successEmailQueue()).to(successEmailExchange()).with(env.getProperty("mq.kill.item.success.email.routing.key"));
    }
    /**
     * 单一消费者
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        return factory;
    }

}
