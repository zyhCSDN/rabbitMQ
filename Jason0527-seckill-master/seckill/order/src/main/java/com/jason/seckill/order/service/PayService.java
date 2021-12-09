package com.jason.seckill.order.service;


import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.jason.seckill.order.entity.OrderRecord;
import com.jason.seckill.order.mapper.SeckillMapper;
import com.jason.seckill.order.sms.SmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PayService {

    private static final Logger logger = LoggerFactory.getLogger(PayService.class);

    @Resource
    private SeckillMapper seckillMapper;

    @Resource
    private Environment env;

    @Resource
    private SmsUtil smsUtil;

    /**
     * 确认是否支付
     * @param orderId
     */
    public void confirmPay(String orderId) throws ClientException {
        OrderRecord orderRecord = seckillMapper.selectNoPayOrderById(orderId);
        //根据订单号校验该用户是否已支付
        if(checkPay(orderId)){
            //已支付
            orderRecord.setPayStatus(1);
            seckillMapper.updatePayStatus(orderRecord);
            logger.info("用户{}已支付",orderId);
            //支付成功，发送短信商品详情
            SendSmsResponse sendSmsResponse = smsUtil.sendSms( env.getProperty( "mobblie" ), env.getProperty( "template_code" ), env.getProperty( "sign_name" ), "哈哈" );

            logger.info("发送手机短信成功{}" ,sendSmsResponse);
        }else{
            //未支付
            orderRecord.setPayStatus(0);
            seckillMapper.updatePayStatus(orderRecord);
            //-超时未支付，商品库存回滚加一
            seckillMapper.reduceGoodsStockById1(orderRecord.getGoodsId());
            logger.info("用户{}未支付",orderId);
            SendSmsResponse sendSmsResponse = smsUtil.sendSms( env.getProperty( "mobblie" ), env.getProperty( "template_code" ), env.getProperty( "sign_name" ), "哈哈" );
            logger.info("发送手机短信失败{}" ,sendSmsResponse);
        }
    }

    /**
     * 模拟判断订单支付成功或失败，成功失败随机
     * @param orderId
     * @return
     */
    public boolean checkPay(String orderId){
        Random random = new Random();
//        该方法的作用是生成一个随机的int值，该值介于[0,n)的区间，也就是0到n之间的随机int值，包含0而不包含n。
        int res = random.nextInt(2);
        logger.info("res======================"+  res  );
        return res==0?false:true;
//        return true;
    }

//    public static void main(String[] args) {
//        PayService payService = new PayService();
//        for (int i = 0; i <10 ; i++) {
//            payService.checkPay( i+"" );
//        }
//    }


    public static void main(String[] args) {

        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("task  run:"+ sdf.format( new Date(  ) ));
//            }
//        };
//        Timer timer = new Timer();
//        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。这里是每3秒执行一次
//        timer.schedule(timerTask,10,3000);

//        该方法跟Timer类似，直接看demo：
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 参数：1、任务体 2、首次执行的延时时间
        //      3、任务执行间隔 4、间隔时间单位
        service.scheduleAtFixedRate(()->System.out.println("task ScheduledExecutorService "+sdf.format( new Date(  ) )), 0, 3, TimeUnit.SECONDS);
    }

}
