package com.xuqianlei.boot.akka.controller;

import com.xuqianlei.boot.akka.entity.process.Process;
import com.xuqianlei.boot.akka.event.Event;
import com.xuqianlei.boot.akka.event.EventCenter;
import com.xuqianlei.boot.akka.event.EventMonitor;
import com.xuqianlei.boot.akka.manager.ProcessManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/10:57
 */
@RestController
public class ProcessController {

    @Autowired
    private EventCenter eventCenter;

    @Autowired
    private EventMonitor eventMonitor;

    @Autowired
    private ProcessManager processManager;


    /**
     * 推送事件
     * @param event
     * @return
     *
     */
    @PostMapping("pushEvent")
    public String pushEvent(@RequestBody Event event) {
        //事件存储
        eventCenter.receiveEvent(event);
        //策略执行
        processManager.executeProcess(event);
        //事件通知
        eventMonitor.adviceActorRef(event);
        return "ok";
    }

    /**
     * 发布策略流
     * @param process
     * @return
     */
    @PostMapping("releaseProcess")
    public String releaseProcess(@RequestBody Process process) {
        processManager.releaseProcess(process);
        return "ok";
    }

}
