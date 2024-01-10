package com.xuqianlei.boot.akka.enums;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/14:28
 */
public enum EventEnum {
    OPEN_APP("open_app", "打开app"),
    WATCH_ADVERTISING("watch_ad", "观看广告"),
    FIRST_REFILL("first_refill", "首次充值"),
    ;

    /**
     * 事件类型
     */
    private final String type;

    /**
     * 描述
     */
    private final String describe;

    public String getType() {
        return type;
    }

    public String getDescribe() {
        return describe;
    }

    EventEnum(String type, String describe) {
        this.type = type;
        this.describe = describe;
    }


    public static EventEnum getEnum(String key) {
        EventEnum[] enums = EventEnum.values();
        for (EventEnum en : enums) {
            if (en.type.equals(key)) {
                return en;
            }
        }
        return null;
    }
}
