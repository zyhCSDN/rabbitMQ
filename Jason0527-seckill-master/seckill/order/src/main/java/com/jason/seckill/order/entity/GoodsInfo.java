package com.jason.seckill.order.entity;

/**
 * 商品库存表
 */

public class GoodsInfo {

    private String id;
    private String goodsName;
    private String goodsStock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsStock() {
        return goodsStock;
    }

    public void setGoodsStock(String goodsStock) {
        this.goodsStock = goodsStock;
    }

    @Override
    public String toString() {
        return "GoodsInfo{" +
                "id='" + id + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsStock='" + goodsStock + '\'' +
                '}';
    }
}
