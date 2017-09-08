package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 回退节点基本信息实体
 * File created on 2017/4/1.
 *
 * @author jkun
 */
public class FallBackNode {
    /**
     * 可退回的任务节点Id
     */
    @JsonProperty("af_nodekey")
    private String nodeKey;

    /**
     * 可退回的任务节点名称
     */
    @JsonProperty("af_nodeName")
    private String nodeName;

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
