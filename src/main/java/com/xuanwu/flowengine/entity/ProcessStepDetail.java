package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.List;

/**
 * 流程当前节点明细对象
 * Created by jkun on 2017/2/14.
 *
 * @author jkun
 */
public class ProcessStepDetail {
    /**
     * 流程定义Id
     */
    @JsonProperty("af_processdefineid")
    private String processDefineId;

    /**
     * 流程定义Key
     */
    @JsonProperty("af_processdefinekey")
    private String processDefineKey;

    /**
     * 流程定义名
     */
    @JsonProperty("af_processdefinename")
    private String processDefineName;

    /**
     * 流程实例Id
     */
    @JsonProperty("af_processinstanceid")
    private String processInstanceId;

    /**
     * 流程实例名
     */
    @JsonProperty("af_processinstancename")
    private String processInstanceName;

    /**
     * 任务实例Id
     */
    @JsonProperty("af_taskid")
    private String taskId;

    /**
     * 任务Key (即为流程定义里task的id)
     */
    @JsonProperty("af_taskkey")
    private String taskKey;

    /**
     * 任务名称 (即为流程定义里task的name)
     */
    @JsonProperty("af_taskname")
    private String taskName;

    /**
     * 任务创建时间
     */
    @JsonProperty("af_createtime")
    private String createTime;

    /**
     * 任务节点关联的UI对象编码
     */
    @JsonProperty("af_taskuiconfig")
    private JsonNode taskUIConfig;

    /**
     * 是否已处理（决定页面审批UI元素的显示或隐藏）
     */
    @JsonProperty("af_hashandle")
    private boolean hasHandle;

    /**
     * 流程是否已结束
     */
    @JsonProperty("af_hasend")
    private boolean hasEnd;

    /**
     * 业务Id
     */
    @JsonProperty("af_businesskey")
    private String businessKey;

    /**
     * 审批选择范围定义
     */
    @JsonProperty("af_choices")
    private Collection<ProcessChoice> choices;

    /**
     * 任务节点是否为会审节点
     */
    @JsonProperty("af_jointnode")
    private boolean jointNode = false;

    /**
     * 流程历史活动追踪List
     */
    @JsonProperty("af_processtraces")
    private List<ProcessTraceInfo> processTraces;

    public String getProcessDefineId() {
        return processDefineId;
    }

    public void setProcessDefineId(String processDefineId) {
        this.processDefineId = processDefineId;
    }

    public String getProcessDefineKey() {
        return processDefineKey;
    }

    public void setProcessDefineKey(String processDefineKey) {
        this.processDefineKey = processDefineKey;
    }

    public String getProcessDefineName() {
        return processDefineName;
    }

    public void setProcessDefineName(String processDefineName) {
        this.processDefineName = processDefineName;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<ProcessTraceInfo> getProcessTraces() {
        return processTraces;
    }

    public void setProcessTraces(List<ProcessTraceInfo> processTraces) {
        this.processTraces = processTraces;
    }

    public boolean isHasHandle() {
        return hasHandle;
    }

    public void setHasHandle(boolean hasHandle) {
        this.hasHandle = hasHandle;
    }

    public boolean isHasEnd() {
        return hasEnd;
    }

    public void setHasEnd(boolean hasEnd) {
        this.hasEnd = hasEnd;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public JsonNode getTaskUIConfig() {
        return taskUIConfig;
    }

    public void setTaskUIConfig(JsonNode taskUIConfig) {
        this.taskUIConfig = taskUIConfig;
    }

    public Collection<ProcessChoice> getChoices() {
        return choices;
    }

    public void setChoices(Collection<ProcessChoice> choices) {
        this.choices = choices;
    }

    public boolean isJointNode() {
        return jointNode;
    }

    public void setJointNode(boolean jointNode) {
        this.jointNode = jointNode;
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
    }
}
