package com.xuqianlei.boot.akka.entity.command;

import com.alibaba.fastjson.JSONObject;
import com.xuqianlei.boot.akka.entity.CommandContext;
import com.xuqianlei.boot.akka.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/09/14:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RootCommandContext implements CommandContext {

    /**
     * 消息事件
     */
    private Event event;

    /**
     * 策略节点信息
     */
    private Map<String, JSONObject> processInfo;

    /**
     * 策略流id
     */
    private Long processId;

}
