package com.xuqianlei.boot.akka.entity.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 开始节点
 * @Author: xuqianlei
 * @Date: 2024/01/08/17:47
 */
@Data
public class StartNode extends Node {

    public StartNode(String type, String key, String name, Next next) {
        super(type, key, name, next);
    }
}
