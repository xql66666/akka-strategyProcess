package com.xuqianlei.boot.akka.actor.root;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuqianlei.boot.akka.actor.node.StartNodeActor;
import com.xuqianlei.boot.akka.config.ActorServiceFactory;
import com.xuqianlei.boot.akka.entity.CommandContext;
import com.xuqianlei.boot.akka.entity.command.NodeCommandContext;
import com.xuqianlei.boot.akka.entity.command.RootCommandContext;
import com.xuqianlei.boot.akka.enums.NodeEnum;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

/**
 * @Description: 实时营销策略流执行行为
 * @Author: xuqianlei
 * @Date: 2024/01/09/14:32
 */
public class RootBehaviorActor {

    public static Behavior<CommandContext> create(ActorServiceFactory actorServiceFactory) {
        return Behaviors.setup(
                context ->
                        Behaviors.receive(CommandContext.class)
                        .onMessage(RootCommandContext.class, n -> {
                            ActorRef<CommandContext> ref = context.spawn(StartNodeActor.create(actorServiceFactory), n.getProcessId() + "_" + UUID.randomUUID());
                            Map<String, JSONObject> processInfo = n.getProcessInfo();
                            JSONObject nodeJson = processInfo.get("main");
                            ref.tell(NodeCommandContext.builder()
                                            .event(n.getEvent())
                                            .processId(n.getProcessId())
                                            .processInfo(processInfo)
                                            .currNode(JSON.parseObject(nodeJson.toJSONString(), (Type) NodeEnum.getClass(nodeJson.getString("type"))))
                                    .build());
                            return Behaviors.same();
                        })
                        .build()
        );
    }
}
