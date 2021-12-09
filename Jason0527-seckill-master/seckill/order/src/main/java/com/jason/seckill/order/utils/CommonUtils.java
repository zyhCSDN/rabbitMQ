package com.jason.seckill.order.utils;

import java.util.UUID;

/**
 * 一些常用的工具
 */
public class CommonUtils {

    /**
     * 生成随机的uuid
     * @return
     */
    public static String createUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }


    /**
     * 字符串是否为空
     * @param string
     * @return boolean of isNull
     */
    public static boolean isNull(String string){
        return string==null?false:string.length()==0?false:true;
    }

    /**
     * 字符串是否为空或者空格
     * @param string
     * @return boolean of isNullOrSpace
     */
    public static boolean isNullOrSpace(String string){
        return (string==null) || ("".equals(string.trim()));
    }

}
