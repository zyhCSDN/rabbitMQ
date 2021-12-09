package com.jason.seckill.order.utils;

import com.jason.seckill.order.services.MyCallback;
import com.jason.seckill.order.sms.Caller;

/**
 * @author: Zhaoyongheng
 * @date: 2020/9/24
 */
public class CallBackTest {
    public static void main(String[] args) {

        Caller caller = new Caller();

        //实例化具体回调函数，实现回调方法
        caller.setMyCallback(new MyCallback() {
            @Override
            public void func() {
                System.out.println("Hello world");
            }
        });

        caller.doCall();
    }
}
