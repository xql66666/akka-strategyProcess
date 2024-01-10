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
import com.xuqianlei.boot.akka.entity.process.ActionNode;
import com.xuqianlei.boot.akka.entity.process.Node;
import com.xuqianlei.boot.akka.entity.process.StartNode;
import com.xuqianlei.boot.akka.entity.process.TimerNode;
import com.xuqianlei.boot.akka.enums.NodeState;
import com.xuqianlei.boot.akka.util.BehaviorCreator;
import lombok.extern.slf4j.Slf4j;


/**
 * @Description: 行为节点actor
 * @Author: xuqianlei
 * @Date: 2024/01/02/13:48
 */
@Slf4j
public class ActionNodeActor extends AbstractBehavior<CommandContext> implements FsmBehavior<ActionNode> {


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
    private ActionNode currNode;


    public static Behavior<CommandContext> create(ActorServiceFactory actorServiceFactory) {
        return Behaviors.setup(context -> Behaviors.withTimers(timers -> new ActionNodeActor(context, actorServiceFactory, timers)));
    }

    public ActionNodeActor(ActorContext<CommandContext> context, ActorServiceFactory actorServiceFactory, TimerScheduler<CommandContext> timerScheduler) {
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
                    log.info("ActionNode节点已停止 " + getContext().getSelf().path());
                    return this;
                })
                .build();
    }


    @Override
    public Behavior<CommandContext> onInit(NodeCommandContext command) {
        NodeCommandContext<ActionNode> commandContext = (NodeCommandContext<ActionNode>) command;
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

        //行为执行
        serviceFactory.actionService.sendMessage(command.getEvent().getCustomerId(), currNode.getChannelType(), currNode.getChannelMsg());

        //构建子节点
        JSONObject nextNodeJson = (JSONObject) command.getProcessInfo().get(next.getKey());
        String nodeType = nextNodeJson.getString("type");
        ActorRef<CommandContext> ref = getContext().spawn(BehaviorCreator.createBehavior(nodeType, serviceFactory), next.getKey());
        command.setCurrNode(BehaviorCreator.createNode(nodeType, nextNodeJson));

        //发送消息
        getContext().watch(ref);
        ref.tell(command);

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