package com.xuqianlei.boot.akka.config;

import com.xuqianlei.boot.akka.event.EventMonitor;
import com.xuqianlei.boot.akka.service.ActionService;
import org.springframework.stereotype.Component;

/**
 * @Description:  actor中所以的bean调用 都由此bean为入口
 * @Author: xuqianlei
 * @Date: 2024/01/08/15:55
 */
@Component
public class ActorServiceFactory {

    /**
     * action服务
     */
    public final ActionService actionService;

    /**
     * 事件监控
     */
    public final EventMonitor eventMonitor;

    public ActorServiceFactory(ActionService actionService, EventMonitor eventMonitor) {
        this.actionService = actionService;
        this.eventMonitor = eventMonitor;
    }
}
