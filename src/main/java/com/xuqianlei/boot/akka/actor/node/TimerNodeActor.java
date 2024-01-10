package com.xuqianlei.boot.akka.actor.node;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
import com.alibaba.fastjson.JSONObject;
import com.xuqianlei.boot.akka.actor.FsmBehavior;
import com.xuqianlei.boot.akka.config.ActorServiceFactory;
import com.xuqianlei.boot.akka.entity.CommandContext;
import com.xuqianlei.boot.akka.entity.command.NodeCommandContext;
import com.xuqianlei.boot.akka.entity.process.Node;
import com.xuqianlei.boot.akka.entity.process.StartNode;
import com.xuqianlei.boot.akka.entity.process.TimerNode;
import com.xuqianlei.boot.akka.enums.NodeState;
import com.xuqianlei.boot.akka.util.BehaviorCreator;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.UUID;


/**
 * @Description: 延迟节点actor
 * @Author: xuqianlei
 * @Date: 2024/01/02/13:48
 */
@Slf4j
public class TimerNodeActor extends AbstractBehavior<CommandContext> implements FsmBehavior<TimerNode> {


    /**
     * 节点状态
     */
    private NodeState state = NodeState.INIT;

    /**
     * 服务调用factory
     */
    private final ActorServiceFactory serviceFactory;

    /**
     * 定时器
     */
    private final TimerScheduler<CommandContext> timerScheduler;

    /**
     * 当前节点
     */
    private TimerNode currNode;


    public static Behavior<CommandContext> create(ActorServiceFactory actorServiceFactory) {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new TimerNodeActor(context, actorServiceFactory, timers)));
    }

    public TimerNodeActor(ActorContext<CommandContext> context, ActorServiceFactory actorServiceFactory, TimerScheduler<CommandContext> timerScheduler) {
        super(context);
        this.serviceFactory = actorServiceFactory;
        this.timerScheduler = timerScheduler;
    }

    @Override
    public Receive<CommandContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(NodeCommandContext.class, m -> NodeState.INIT.equals(state), this::onInit)
                .onMessage(NodeCommandContext.class, m -> NodeState.RUNNING.equals(state), this::onRunning)
                .onMessage(NodeCommandContext.class, m -> NodeState.WAIT_INVOKE.equals(state), this::onInvoke)
                .onMessage(NodeCommandContext.class, m -> NodeState.FINISH.equals(state), this::onFinish)
                .onSignal(Terminated.class, signal -> Behaviors.stopped())
                .onSignal(PostStop.class, signal -> {
                    log.info("TimerNode节点已停止 " + getContext().getSelf().path());
                    return this;
                })
                .build();
    }


    @Override
    public Behavior<CommandContext> onInit(NodeCommandContext command) {
        NodeCommandContext<TimerNode> commandContext = (NodeCommandContext<TimerNode>) command;
        currNode = commandContext.getCurrNode();

        log.info("节点启动[" + currNode.getName() + "]");
        state = NodeState.RUNNING;
        return onRunning(command);
    }

    @Override
    public Behavior<CommandContext> onRunning(NodeCommandContext command) {
        //无子节点则停止
        Node.Next next = currNode.getNext();
        if (next == null) {
            return Behaviors.stopped();
        }

        //构建子节点
        JSONObject nextNodeJson = (JSONObject) command.getProcessInfo().get(next.getKey());
        String nodeType = nextNodeJson.getString("type");
        ActorRef<CommandContext> ref = getContext().spawn(BehaviorCreator.createBehavior(nodeType, serviceFactory), next.getKey());
        command.setCurrNode(BehaviorCreator.createNode(nodeType, nextNodeJson));

        //发送消息
        getContext().watch(ref);
        getContext().scheduleOnce(Duration.ofSeconds(currNode.getTimeout()), ref, command);

        state = NodeState.WAIT_INVOKE;
        return onInvoke(command);
    }

    @Override
    public Behavior<CommandContext> onInvoke(NodeCommandContext command) {
        state = NodeState.FINISH;
        return onFinish(command);
    }

    @Override
    public Behavior<CommandContext> onFinish(NodeCommandContext command) {
        log.info("节点执行完成[" + currNode.getName() + "]");
        return Behaviors.same();
    }

}