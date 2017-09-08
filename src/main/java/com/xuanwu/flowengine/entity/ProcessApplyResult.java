package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 流程发起结果Entity
 * File created on 2017/4/8.
 *
 * @author jkun
 */
public class ProcessApplyResult {

    /**
     * 流程定义Id
     */
    @JsonProperty("af_processdefineid")
    private String processDefineId;

    /**
     * 流程实例Id
     */
    @JsonProperty("af_processinstanceid")
    private String processInstanceId;

    /**
     * 流程发起后的第一步任务实例Id
     */
    @JsonProperty("af_taskid")
    private String currentTaskId;

    /**
     * 流程发起后的第一步任务名称
     */
    @JsonProperty("af_taskname")
    private String currentTaskName;

    /**
     * 业务数据标识值
     */
    @JsonProperty("af_businesskey")
    private String businessKey;

    public String getProcessDefineId() {
        return processDefineId;
    }

    public void setProcessDefineId(String processDefineId) {
        this.processDefineId = processDefineId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(String currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getCurrentTaskName() {
        return currentTaskName;
    }

    public void setCurrentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
    }
}
