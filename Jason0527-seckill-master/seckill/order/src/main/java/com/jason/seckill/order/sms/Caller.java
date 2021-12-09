package com.jason.seckill.order.sms;

import com.jason.seckill.order.services.MyCallback;

/**
 * @author: Zhaoyongheng
 * @date: 2020/9/24
 */
public class Caller {

    private MyCallback myCallback;

    public void doCall(){
        myCallback.func();
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }
}
