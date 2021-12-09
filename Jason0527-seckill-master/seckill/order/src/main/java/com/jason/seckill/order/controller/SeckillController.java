package com.jason.seckill.order.controller;

import com.jason.seckill.order.enums.StatusCode;
import com.jason.seckill.order.request.OrderRequest;
import com.jason.seckill.order.service.CheckService;
import com.jason.seckill.order.service.RabbitSenderService;
import com.jason.seckill.order.service.SeckillService;
import com.jason.seckill.order.utils.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀controller
 */
@RequestMapping("seckill")
@RestController
public class SeckillController {

    private static final Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private CheckService checkService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RabbitSenderService rabbitSenderService;

    /**
     * 秒杀入口
     *
     * @param order 下单参数
     * @return
     */
    @RequestMapping(value = "go", method = RequestMethod.POST,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public BaseResponse seckillGoods( OrderRequest order) {

        BaseResponse response = new BaseResponse(StatusCode.Success);
//        List<OrderRequest> list = new ArrayList<>(  );

        try {
            //判断该用户是否首次秒杀该商品
            for (int i =0;i<100;i++){
                order.setUserId( i+"");
                if (checkService.checkSeckillUser(order,i)) {
                    //信息保存redis成功之后 向mq发送消息队列
                    seckillService.seckill(order);
                    response.setData( order );
                    //秒杀成功，异步向rabbitmq发送消息
                    rabbitSenderService.sendKillSuccessEmailMsg(order.getGoodsId());
                }
                response.setMsg( "您已经秒杀过该商品，不能再次秒杀" );
//                list.add( order );
            }

//            response.setData( list );
        } catch (Exception e) {
            logger.info(e.getMessage());
            response = new BaseResponse(StatusCode.Fail);
        }
        return response;
    }


    @RequestMapping(value = "go1")
    @ResponseBody
    public BaseResponse seckillGoodsInfo( ) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        response.setMsg( "商品秒杀成功" );
        response.setData( "您成功秒杀了商品，请立即付款！！！" );
        return response;
    }
}
