package com.xuqianlei.boot.akka.util;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuqianlei.boot.akka.actor.node.ActionNodeActor;
import com.xuqianlei.boot.akka.actor.node.DoneNodeActor;
import com.xuqianlei.boot.akka.actor.node.StartNodeActor;
import com.xuqianlei.boot.akka.actor.node.SwitchNodeActor;
import com.xuqianlei.boot.akka.actor.node.TimerNodeActor;
import com.xuqianlei.boot.akka.config.ActorServiceFactory;
import com.xuqianlei.boot.akka.constant.NodeType;
import com.xuqianlei.boot.akka.entity.CommandContext;
import com.xuqianlei.boot.akka.entity.process.ActionNode;
import com.xuqianlei.boot.akka.entity.process.DoneNode;
import com.xuqianlei.boot.akka.entity.process.Node;
import com.xuqianlei.boot.akka.entity.process.StartNode;
import com.xuqianlei.boot.akka.entity.process.SwitchNode;
import com.xuqianlei.boot.akka.entity.process.TimerNode;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/09/17:03
 */
public class BehaviorCreator {

    /**
     * 创建行为
     * @param nodeType
     * @param serviceFactory
     * @return
     */
    public static Behavior<CommandContext> createBehavior(String nodeType, ActorServiceFactory serviceFactory) {
        switch (nodeType) {
            case NodeType.START_NODE :
                return StartNodeActor.create(serviceFactory);
            case NodeType.ACTION_NODE:
                return ActionNodeActor.create(serviceFactory);
            case NodeType.SWITCH_NODE:
                return SwitchNodeActor.create(serviceFactory);
            case NodeType.TIMER_NODE:
                return TimerNodeActor.create(serviceFactory);
            case NodeType.DONE_NODE:
                return DoneNodeActor.create(serviceFactory);
            default:
                throw new RuntimeException();
        }
   }

    /**
     * 创建currNode class
     * @param nodeType
     * @param nextNodeJson
     * @return
     */
   public static Node createNode(String nodeType, JSONObject nextNodeJson) {
       switch (nodeType) {
           case NodeType.START_NODE :
               return JSON.parseObject(nextNodeJson.toJSONString(), StartNode.class);
           case NodeType.ACTION_NODE:
               return JSON.parseObject(nextNodeJson.toJSONString(), ActionNode.class);
           case NodeType.SWITCH_NODE:
               return JSON.parseObject(nextNodeJson.toJSONString(), SwitchNode.class);
           case NodeType.TIMER_NODE:
               return JSON.parseObject(nextNodeJson.toJSONString(), TimerNode.class);
           case NodeType.DONE_NODE:
               return JSON.parseObject(nextNodeJson.toJSONString(), DoneNode.class);
           default:
               throw new RuntimeException();
       }
   }
}
