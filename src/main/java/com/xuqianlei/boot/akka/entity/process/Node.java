package com.xuqianlei.boot.akka.entity.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:
 * @Author: xuqianlei
 * @Date: 2024/01/08/17:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Node implements Serializable {

    static final long serialVersionUID = 1L;

    /**
     * 节点类型
     */
    private String type;

    /**
     * 节点key
     */
    private String key;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 默认的下一个节点
     */
    private Next next;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Next implements Serializable {

        static final long serialVersionUID = 1L;

        /**
         * 节点key
         */
        private String key;
    }



}
