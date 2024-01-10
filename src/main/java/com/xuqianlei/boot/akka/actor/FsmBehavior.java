package com.xuqianlei.boot.akka.actor;

import akka.actor.typed.Behavior;
import com.xuqianlei.boot.akka.entity.CommandContext;
import com.xuqianlei.boot.akka.entity.command.NodeCommandContext;
import com.xuqianlei.boot.akka.entity.process.Node;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/14:05
 */
public interface FsmBehavior<T extends Node> {

    /**
     * 初始化
     * @param command
     * @return
     */
    Behavior<CommandContext> onInit(NodeCommandContext<T> command);

    /**
     * 开始执行
     * @param command
     * @return
     */
    Behavior<CommandContext> onRunning(NodeCommandContext<T> command);

    /**
     * 等待调用
     * @param command
     * @return
     */
    Behavior<CommandContext> onInvoke(NodeCommandContext<T> command);

    /**
     * 结束执行
     * @param command
     * @return
     */
    Behavior<CommandContext> onFinish(NodeCommandContext<T> command);

}
