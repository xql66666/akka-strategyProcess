package com.xuqianlei.boot.akka.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/14:22
 */
@Component
@Slf4j
public class EventCenter {

    /**
     * 事件列表
     */
    private List<Event> events = new ArrayList<>();

    private static final AtomicLong seq = new AtomicLong(0);

    /**
     * 接收事件
     * @param event
     */
    public void receiveEvent(Event event) {
        event.setSeq(seq.getAndIncrement());
        events.add(event);
    }


    /**
     * 根据条件查找时间
     * @param seq 事件起始序号
     * @param eventType
     * @param customerId
     * @return
     */
    public Event lookupEvent(Integer seq, String eventType, String customerId) {
        for (int i = seq + 1; i < events.size(); i++) {
            Event event = events.get(i);
            if (customerId.equals(event.getCustomerId()) && eventType.equals(event.getType())) {
                return event;
            }
        }
        return null;
    }






}
