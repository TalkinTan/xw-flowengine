package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * 流程提交参数对象
 * File created on 2017/5/26.
 *
 * @author jkun
 */
public class ProcessSubmitParam {
    /**
     * 任务实例Id
     */
    @JsonProperty("af_taskid")
    private String taskId;

    /**
     * 流程实例Id
     */
    @JsonProperty("af_processinstanceid")
    private String processInstanceId;

    /**
     * 审批选择Code
     */
    @JsonProperty("af_choice")
    private Integer choice;

    /**
     * 审批选择Value
     */
    @JsonProperty("af_choicename")
    private String choiceName;

    /**
     * 审批意见
     */
    @JsonProperty("af_comments")
    private String comments;

    /**
     * 下一步节点是否会审节点
     */
    @JsonProperty("af_jointnode")
    private boolean nextStepIsMultiInstance;

    /**
     * 当前节点是否为会审节点
     */
    @JsonProperty("af_currentjoinnode")
    private boolean currentStepIsMultiInstance;

    /**
     * 下一步人员Id，多个用逗号分隔
     */
    @JsonProperty("af_nextstepusers")
    private String nextStepUsers;

    /**
     * 回退时选择的回退节点Key
     */
    @JsonProperty("af_fallbacknodekey")
    private String fallBackNodeKey;

    /**
     * 业务对象名称
     */
    @JsonProperty("af_businessobjname")
    private String businessObjName;

    /**
     * 业务对象标识属性名称
     */
    @JsonProperty("af_businessobjpropertyname")
    private String businessObjPropertyName;

    /**
     * 业务数据Map
     */
    private Map<String, Object> bizData;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Integer getChoice() {
        return choice;
    }

    public void setChoice(Integer choice) {
        this.choice = choice;
    }

    public String getChoiceName() {
        return choiceName;
    }

    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isNextStepIsMultiInstance() {
        return nextStepIsMultiInstance;
    }

    public void setNextStepIsMultiInstance(boolean nextStepIsMultiInstance) {
        this.nextStepIsMultiInstance = nextStepIsMultiInstance;
    }

    public String getNextStepUsers() {
        return nextStepUsers;
    }

    public void setNextStepUsers(String nextStepUsers) {
        this.nextStepUsers = nextStepUsers;
    }

    public String getFallBackNodeKey() {
        return fallBackNodeKey;
    }

    public void setFallBackNodeKey(String fallBackNodeKey) {
        this.fallBackNodeKey = fallBackNodeKey;
    }

    public Map<String, Object> getBizData() {
        return bizData;
    }

    public void setBizData(Map<String, Object> bizData) {
        this.bizData = bizData;
    }

    public String getBusinessObjName() {
        return businessObjName;
    }

    public void setBusinessObjName(String businessObjName) {
        this.businessObjName = businessObjName;
    }

    public String getBusinessObjPropertyName() {
        return businessObjPropertyName;
    }

    public void setBusinessObjPropertyName(String businessObjPropertyName) {
        this.businessObjPropertyName = businessObjPropertyName;
    }

    public boolean isCurrentStepIsMultiInstance() {
        return currentStepIsMultiInstance;
    }

    public void setCurrentStepIsMultiInstance(boolean currentStepIsMultiInstance) {
        this.currentStepIsMultiInstance = currentStepIsMultiInstance;
    }
}
