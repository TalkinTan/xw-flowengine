package com.xuanwu.flowengine.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 审批消息参数
 * File created on 2017/8/7.
 *
 * @author jkun
 */
public class FlowMessageParam {
    /**
     * 流程实例Id
     */
    @JsonProperty("af_processinstanceid")
    private String processInstanceId;

    /**
     * 流程定义Id
     */
    @JsonProperty("af_processdefineid")
    private String processDefineId;

    /**
     * 流程定义名称
     */
    @JsonProperty("af_processdefinename")
    private String processDefineName;

    /**
     * 流程实例名称
     */
    @JsonProperty("af_processinstancename")
    private String processInstanceName;

    /**
     * 发起人成员Id
     */
    @JsonProperty("af_applyusercode")
    private String applyUserCode;

    /**
     * 流程是否已结束
     */
    @JsonProperty("af_hasend")
    private boolean hasEnd;

    /**
     * 流程提交后的任务实例状态列表
     */
    @JsonProperty("forwardstate")
    private List<TaskBasic> forwardTaskState;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefineId() {
        return processDefineId;
    }

    public void setProcessDefineId(String processDefineId) {
        this.processDefineId = processDefineId;
    }

    public String getProcessDefineName() {
        return processDefineName;
    }

    public void setProcessDefineName(String processDefineName) {
        this.processDefineName = processDefineName;
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
    }

    public String getApplyUserCode() {
        return applyUserCode;
    }

    public void setApplyUserCode(String applyUserCode) {
        this.applyUserCode = applyUserCode;
    }

    public boolean isHasEnd() {
        return hasEnd;
    }

    public void setHasEnd(boolean hasEnd) {
        this.hasEnd = hasEnd;
    }

    public List<TaskBasic> getForwardTaskState() {
        return forwardTaskState;
    }

    public void setForwardTaskState(List<TaskBasic> forwardTaskState) {
        this.forwardTaskState = forwardTaskState;
    }
}