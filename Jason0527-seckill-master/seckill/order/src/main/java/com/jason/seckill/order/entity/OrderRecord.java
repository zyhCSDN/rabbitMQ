package com.jason.seckill.order.entity;

/**
 * 下单记录表
 */
public class OrderRecord {

    private String id;
    private String userId;
    private String goodsId;
    /**
     * 0-超时未支付  1-已支付  2-待支付
     */
    private Integer payStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    @Override
    public String toString() {
        return "OrderRecord{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", goodsId='" + goodsId + '\'' +
                '}';
    }
}
