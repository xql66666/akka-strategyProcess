package com.xuqianlei.boot.akka.event;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/14:23
 */
public class Event {

    private Long seq;
    private String type;
    private String customerId;
    private String msg;

    public Event() {
    }

    public Event(Long seq, String type, String customerId, String msg) {
        this.seq = seq;
        this.type = type;
        this.customerId = customerId;
        this.msg = msg;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
