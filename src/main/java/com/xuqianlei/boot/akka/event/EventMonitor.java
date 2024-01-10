package com.xuqianlei.boot.akka.event;

import akka.actor.typed.ActorRef;
import com.xuqianlei.boot.akka.entity.command.NodeCommandContext;
import com.xuqianlei.boot.akka.enums.EventEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/14:59
 */
@Component
public class EventMonitor {


    @Autowired
    private EventCenter eventCenter;

    /**
     * 监听器actor ref列表
     */
    private Map<EventEnum, Map<String, List<Pair<ActorRef, NodeCommandContext>>>> monitorActorMap = new ConcurrentHashMap<>();


    /**
     * 注册actor
     * @param type
     * @param commandContext
     * @param ref
     */
    public synchronized void registerActorRef(EventEnum type, NodeCommandContext commandContext, ActorRef ref) {
        monitorActorMap.computeIfAbsent(type, k -> new ConcurrentHashMap<>(64));
        //客户号
        String customerId = commandContext.getEvent().getCustomerId();
        monitorActorMap.get(type).computeIfAbsent(customerId, k -> new ArrayList<>());
        monitorActorMap.get(type).get(customerId).add(Pair.of(ref, commandContext));

        //查询历史事件
        Event startEvent = commandContext.getEvent();
        Event event = eventCenter.lookupEvent(Math.toIntExact(startEvent.getSeq()), type.getType(), startEvent.getCustomerId());
        if (event != null) {
            commandContext.getExtraEvent().add(event);
            ref.tell(commandContext);
        }
    }

    /**
     * 通知actor
     * @param event
     */
    public void adviceActorRef(Event event) {
        Map<String, List<Pair<ActorRef, NodeCommandContext>>> map = monitorActorMap.get(EventEnum.getEnum(event.getType()));
        if (map != null) {
            List<Pair<ActorRef, NodeCommandContext>> refs = map.get(event.getCustomerId());
            if (!CollectionUtils.isEmpty(refs)) {
                for (int i = refs.size() - 1; i >= 0; i--) {
                    Pair<ActorRef, NodeCommandContext> pair = refs.get(i);
                    pair.getRight().getExtraEvent().add(event);
                    pair.getLeft().tell(pair.getRight());
                    refs.remove(i);
                }
            }
        }
    }


}