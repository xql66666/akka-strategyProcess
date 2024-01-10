package com.xuqianlei.boot.akka.manager;

import akka.actor.typed.ActorSystem;
import com.alibaba.fastjson.JSONObject;
import com.xuqianlei.boot.akka.entity.CommandContext;
import com.xuqianlei.boot.akka.entity.command.RootCommandContext;
import com.xuqianlei.boot.akka.entity.process.Process;
import com.xuqianlei.boot.akka.enums.EventEnum;
import com.xuqianlei.boot.akka.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description: process结构管理
 * @Author: xuqianlei
 * @Date: 2024/01/09/09:59
 */
@Component
@Slf4j
public class ProcessManager {


    @Autowired
    private ActorSystem<CommandContext> actorSystem;

    /**
     * 策略启动事件
     */
    private Map<EventEnum, Set<Long>> executeRuleProcessMap = new HashMap<>();

    /**
     * 策略流节点信息
     */
    private Map<Long, Map<String, JSONObject>> processNodeMap = new HashMap<>();


    /**
     * 发布策略
     */
    public synchronized void releaseProcess(Process process) {
        //策略信息
        Map<String, JSONObject> nodeMap = process.getNodes().stream().collect(Collectors.toMap(k -> k.getString("key"), v -> v));
        processNodeMap.put(process.getProcessId(), nodeMap);

        //策略触发
        EventEnum eventEnum = EventEnum.getEnum(process.getStartEvent());
        executeRuleProcessMap.computeIfAbsent(eventEnum, k -> new HashSet<>());
        executeRuleProcessMap.get(eventEnum).add(process.getProcessId());

        log.info("策略流添加成功:" + process.getProcessId());
    }

    /**
     * 策略执行
     * @param event
     */
    public void executeProcess(Event event) {
        Set<Long> processIds = executeRuleProcessMap.get(EventEnum.getEnum(event.getType()));
        if (!CollectionUtils.isEmpty(processIds)) {
            processIds.forEach(processId -> {
                actorSystem.tell(new RootCommandContext(event, processNodeMap.get(processId), processId));
            });
        }

    }




}
