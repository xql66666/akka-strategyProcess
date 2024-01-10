package com.xuqianlei.boot.akka.entity.process;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/17:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Process {


    /**
     * 策略流id
     */
    private Long processId;

    /**
     * 匹配事件
     */
    private String startEvent;


    /**
     * 节点列表
     */
    private List<JSONObject> nodes;



}
