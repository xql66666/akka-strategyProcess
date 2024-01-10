package com.xuqianlei.boot.akka.entity.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 延时节点
 * @Author: xuqianlei
 * @Date: 2024/01/08/17:47
 */
@Data
public class TimerNode extends Node {

    /**
     * 延迟时间-秒
     */
    private Integer timeout;

    public TimerNode(String type, String key, String name, Next next, Integer timeout) {
        super(type, key, name, next);
        this.timeout = timeout;
    }
}
