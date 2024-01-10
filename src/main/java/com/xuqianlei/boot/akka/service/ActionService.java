package com.xuqianlei.boot.akka.service;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/09/15:27
 */
public interface ActionService {

    /**
     * action
     * @param channelType
     * @param message
     */
    void sendMessage(String customerId, String channelType, String message);
}
