package com.xuqianlei.boot.akka.enums;

import com.xuqianlei.boot.akka.entity.process.ActionNode;
import com.xuqianlei.boot.akka.entity.process.DoneNode;
import com.xuqianlei.boot.akka.entity.process.StartNode;
import com.xuqianlei.boot.akka.entity.process.SwitchNode;
import com.xuqianlei.boot.akka.entity.process.TimerNode;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/14:28
 */
public enum NodeEnum {
    START_NODE("start_node", StartNode.class),
    DONE_NODE("done_node", DoneNode.class),
    ACTION_NODE("action_node", ActionNode.class),
    TIMER_NODE("timer_node", TimerNode.class),
    SWITCH_NODE("switch_node", SwitchNode.class),
    ;

    /**
     * 事件类型
     */
    public final String type;

    /**
     * 描述
     */
    public final Class clazz;

    NodeEnum(String type, Class clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public static NodeEnum getEnum(String key) {
        NodeEnum[] enums = NodeEnum.values();
        for (NodeEnum en : enums) {
            if (en.type.equals(key)) {
                return en;
            }
        }
        return null;
    }

    public static Class getClass(String key) {
        NodeEnum[] enums = NodeEnum.values();
        for (NodeEnum en : enums) {
            if (en.type.equals(key)) {
                return en.clazz;
            }
        }
        return null;
    }
}
