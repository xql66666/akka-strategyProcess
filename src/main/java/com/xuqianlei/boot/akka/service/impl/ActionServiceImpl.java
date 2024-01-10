package com.xuqianlei.boot.akka.service.impl;

import com.xuqianlei.boot.akka.service.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/09/15:27
 */
@Service
@Slf4j
public class ActionServiceImpl implements ActionService {


    @Override
    public void sendMessage(String customerId, String channelType, String message) {
        log.info(String.format("向[%s]渠道的[%s]客户发送了一条消息: %s", channelType, customerId, message));
    }

}
