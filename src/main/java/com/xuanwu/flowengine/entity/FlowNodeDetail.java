package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.util.WorkflowUtils;

import java.util.Collection;

/**
 * 节点信息详情实体类
 * Created by jkun on 2017/3/3.
 *
 * @author jkun
 */
public class FlowNodeDetail {
    /**
     * 节点Key
     */
    @JsonProperty("af_nodekey")
    private String nodeKey;

    /**
     * 节点类型（startEvent, userTask ...）
     */
    @JsonProperty("af_nodetype")
    private String nodeType;

    /**
     * 节点类型名
     */
    @JsonProperty("af_typename")
    private String typeName;

    /**
     * 节点名
     */
    @JsonProperty("af_nodename")
    private String nodeName;

    /**
     * 是否会签节点
     */
    @JsonProperty("af_jointnode")
    private boolean jointNode = false;

    /**
     * 模式（认领模式|选人模式）
     */
    @JsonProperty("af_mode")
    private String mode;

    /**
     * 节点候选人员
     */
    @JsonProperty("af_nodecandidates")
    private Collection<MemberBasicInfoDto> nodeCandidates;

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean isJointNode() {
        return jointNode;
    }

    public void setJointNode(boolean jointNode) {
        this.jointNode = jointNode;
    }

    public String getTypeName() {
        return WorkflowUtils.parseTypeName(nodeType);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Collection<MemberBasicInfoDto> getNodeCandidates() {
        return nodeCandidates;
    }

    public void setNodeCandidates(Collection<MemberBasicInfoDto> nodeCandidates) {
        this.nodeCandidates = nodeCandidates;
    }
}
