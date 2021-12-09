package com.jason.seckill.order.request;

import java.io.Serializable;

/**
 * 抢单请求封装实体类
 */
public class OrderRequest implements Serializable {

    private String userId;
    private String goodsId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }
}
