package com.jason.seckill.order.entity;/**
 * Created by Administrator on 2019/6/22.
 */


import java.io.Serializable;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2019/6/22 10:11
 **/

public class MailDto implements Serializable{
    //邮件主题
    private String subject;
    //邮件内容
    private String content;
    //接收人
    private String[] tos;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getTos() {
        return tos;
    }

    public void setTos(String[] tos) {
        this.tos = tos;
    }

    public MailDto(String property, String content, String[] ss) {
    }
    public MailDto() {
    }

}