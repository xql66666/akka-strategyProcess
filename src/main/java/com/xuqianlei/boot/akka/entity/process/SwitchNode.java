package com.xuqianlei.boot.akka.entity.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 条件节点
 * @Author: xuqianlei
 * @Date: 2024/01/08/17:47
 */
@Data
public class SwitchNode extends Node {

    /**
     * 匹配事件
     */
    private String ruleEvent;

    /**
     * 延迟时间-秒
     */
    private Integer timeout;

    /**
     * 规则匹配next
     */
    private Node.Next ruleNext;

    public SwitchNode(String type, String key, String name, Next next, String ruleEvent, Integer timeout, Next ruleNext) {
        super(type, key, name, next);
        this.ruleEvent = ruleEvent;
        this.timeout = timeout;
        this.ruleNext = ruleNext;
    }
}
