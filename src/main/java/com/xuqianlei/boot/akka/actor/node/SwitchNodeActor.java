package com.xuqianlei.boot.akka.actor.node;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import akka.actor.typed.javadsl.TimerScheduler;
import com.alibaba.fastjson.JSONObject;
import com.xuqianlei.boot.akka.actor.FsmBehavior;
import com.xuqianlei.boot.akka.config.ActorServiceFactory;
import com.xuqianlei.boot.akka.entity.CommandContext;
import com.xuqianlei.boot.akka.entity.command.NodeCommandContext;
import com.xuqianlei.boot.akka.entity.process.DoneNode;
import com.xuqianlei.boot.akka.entity.process.Node;
import com.xuqianlei.boot.akka.entity.process.StartNode;
import com.xuqianlei.boot.akka.entity.process.SwitchNode;
import com.xuqianlei.boot.akka.enums.EventEnum;
import com.xuqianlei.boot.akka.enums.NodeState;
import com.xuqianlei.boot.akka.util.BehaviorCreator;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;


/**
 * @Description: 行为节点actor
 * @Author: xuqianlei
 * @Date: 2024/01/02/13:48
 */
@Slf4j
public class SwitchNodeActor extends AbstractBehavior<CommandContext> implements FsmBehavior<SwitchNode> {


    /**
     * 节点状态
     */
    private NodeState state = NodeState.INIT;

    /**
     * 定时器
     */
    private final TimerScheduler<CommandContext> timerScheduler;

    /**
     * 服务调用factory
     */
    private final ActorServiceFactory serviceFactory;

    /**
     * 当前节点
     */
    private SwitchNode currNode;


    public static Behavior<CommandContext> create(ActorServiceFactory actorServiceFactory) {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new SwitchNodeActor(context, actorServiceFactory, timers)));
    }

    public SwitchNodeActor(ActorContext<CommandContext> context, ActorServiceFactory actorServiceFactory, TimerScheduler<CommandContext> timerScheduler) {
        super(context);
        this.serviceFactory = actorServiceFactory;
        this.timerScheduler = timerScheduler;
    }

    @Override
    public Receive<CommandContext> createReceive() {
        ReceiveBuilder<CommandContext> builder = newReceiveBuilder();
        return builder
                .onMessage(NodeCommandContext.class, m -> NodeState.INIT.equals(state), this::onInit)
                .onMessage(NodeCommandContext.class, m -> NodeState.RUNNING.equals(state), this::onRunning)
                .onMessage(NodeCommandContext.class, m -> NodeState.WAIT_INVOKE.equals(state), this::onInvoke)
                .onMessage(NodeCommandContext.class, m -> NodeState.FINISH.equals(state), this::onFinish)
                .onSignal(Terminated.class, signal -> Behaviors.stopped())
                .onSignal(PostStop.class, signal -> {
                    log.info("SwitchNode节点已停止 " + getContext().getSelf().path());
                    return this;
                })
                .build();
    }


    @Override
    public Behavior<CommandContext> onInit(NodeCommandContext command) {
        NodeCommandContext<SwitchNode> commandContext = (NodeCommandContext<SwitchNode>) command;
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

        state = NodeState.WAIT_INVOKE;
        //延时调用
        timerScheduler.startSingleTimer(currNode.getKey(), command, Duration.ofSeconds(currNode.getTimeout()));

        //注册监听事件
        serviceFactory.eventMonitor.registerActorRef(EventEnum.getEnum(currNode.getRuleEvent()), command, getContext().getSelf());
        log.info(currNode.getKey() + "节点等待通知中...");
        return Behaviors.same();
    }

    @Override
    public Behavior<CommandContext> onInvoke(NodeCommandContext command) {
        String nextKey;
        if (timerScheduler.isTimerActive(currNode.getKey())) {
            timerScheduler.cancel(currNode.getKey());
            //响应
            nextKey = currNode.getRuleNext().getKey();
            log.info(currNode.getKey() + "节点收到事件");
        } else {
            //未响应
            nextKey = currNode.getNext().getKey();
            log.info(currNode.getKey() + "节点未收到事件");
        }

        //构建子节点
        JSONObject nextNodeJson = (JSONObject) command.getProcessInfo().get(nextKey);
        String nodeType = nextNodeJson.getString("type");
        ActorRef<CommandContext> ref = getContext().spawn(BehaviorCreator.createBehavior(nodeType, serviceFactory), nextKey);
        command.setCurrNode(BehaviorCreator.createNode(nodeType, nextNodeJson));

        //发送消息
        getContext().watch(ref);
        ref.tell(command);

        state = NodeState.FINISH;
        return onFinish(command);
    }

    @Override
    public Behavior<CommandContext> onFinish(NodeCommandContext command) {
        log.info("节点执行完成[" + currNode.getName() + "]");
        return Behaviors.same();
    }

}