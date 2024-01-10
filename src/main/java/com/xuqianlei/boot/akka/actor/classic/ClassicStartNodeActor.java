package com.xuqianlei.boot.akka.actor.classic;

import akka.actor.AbstractActor;
import com.xuqianlei.boot.akka.enums.NodeState;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description: 实时营销开始节点
 * @Author: xuqianlei
 * @Date: 2024/01/08/14:16
 */
@Component("startNodeActor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClassicStartNodeActor extends AbstractActor {

    /**
     * 节点状态
     */
    private NodeState state = NodeState.INIT;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, n -> {
                    System.out.println("actor====" + n);
                })
                .build();
    }



}
