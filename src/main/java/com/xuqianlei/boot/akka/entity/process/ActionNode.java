package com.xuqianlei.boot.akka.entity.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 行为节点
 * @Author: xuqianlei
 * @Date: 2024/01/08/17:47
 */
@Data
public class ActionNode extends Node {

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 渠道消息
     */
    private String channelMsg;


    public ActionNode(String type, String key, String name, Next next, String channelType, String channelMsg) {
        super(type, key, name, next);
        this.channelType = channelType;
        this.channelMsg = channelMsg;
    }
}
