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
     * {
     *     "processId": 11111111,
     *     "startEvent": "open_app",
     *     "nodes": [
     *         {
     *             "type": "start_node",
     *             "key": "main",
     *             "name": "开始节点-打开app",
     *             "next": {
     *                 "key": "tn1"
     *             }
     *         },
     *         {
     *             "type": "timer_node",
     *             "key": "tn1",
     *             "name": "延迟5秒",
     *             "next": {
     *                 "key": "an1"
     *             },
     *             "timeout": 5
     *         },
     *         {
     *             "type": "action_node",
     *             "key": "an1",
     *             "name": "推送广告",
     *             "next": {
     *                 "key": "sn1"
     *             },
     *             "channelType": "app",
     *             "channelMsg": "这是一条广告~~~"
     *         },
     *         {
     *             "type": "switch_node",
     *             "key": "sn1",
     *             "name": "判断30秒是否打开广告",
     *             "next": {
     *                 "key": "dn1"
     *             },
     *             "ruleEvent": "watch_ad",
     *             "timeout": 30,
     *             "ruleNext": {
     *                 "key": "an2"
     *             }
     *         },
     *         {
     *             "type": "done_node",
     *             "key": "dn1",
     *             "name": "未观看广告-结束",
     *             "next": null
     *         },
     *         {
     *             "type": "action_node",
     *             "key": "an2",
     *             "name": "推送1小时app会员",
     *             "next": {
     *                 "key": "dn2"
     *             },
     *             "channelType": "app",
     *             "channelMsg": "送你一小时会员~~~"
     *         },
     *         {
     *             "type": "done_node",
     *             "key": "dn2",
     *             "name": "观看广告-结束",
     *             "next": null
     *         }
     *     ]
     * }
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
     *
     * {
     *   "type": "open_app",
     *   "customerId": "wx_szwan",
     *   "msg": "我打开app了"
     * }
     *
     * {
     *   "type": "watch_ad",
     *   "customerId": "wx_szwan",
     *   "msg": "我看广告了~~~"
     * }
     */
    @PostMapping("releaseProcess")
    public String releaseProcess(@RequestBody Process process) {
        processManager.releaseProcess(process);
        return "ok";
    }

}
