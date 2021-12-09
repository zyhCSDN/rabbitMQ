package com.jason.seckill.order.enums;

/**
 * 返回状态
 */
public enum StatusCode {

    Success(200, "成功"),
    Fail(100, "失败");

    private Integer code;
    private String msg;

    StatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
