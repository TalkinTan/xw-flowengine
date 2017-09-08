package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * 用户任务待办对象
 * Created by jkun on 2017/2/13.
 *
 * @author jkun
 */
public class TaskEntityWrapper {
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
     * 流程实例名称
     */
    @JsonProperty("af_processinstancename")
    private String processInstanceName;

    /**
     * 流程定义Key
     */
    @JsonProperty("af_processdefinekey")
    private String processDefineKey;

    /**
     * 流程定义名称
     */
    @JsonProperty("af_processdefinename")
    private String processDefineName;

    /**
     * 待办任务Id
     */
    @JsonProperty("af_taskid")
    private String taskId;

    /**
     * 待办任务Key
     */
    @JsonProperty("af_taskkey")
    private String taskKey;

    /**
     * 待办任务名称
     */
    @JsonProperty("af_taskname")
    private String taskName;

    /**
     * 任务创建时间
     */
    @JsonProperty("af_createtime")
    private Date createTime;

    /**
     * 任务的完成时间
     */
    @JsonProperty("af_endtime")
    private Date endTime;

    /**
     * 是否已设置处理人
     */
    @JsonProperty("af_assigned")
    private Boolean hasAssigned;

    /**
     * 发起人编码
     */
    @JsonProperty("af_applyusercode")
    private String applyUserCode;

    /**
     * 发起人姓名
     */
    @JsonProperty("af_applyusername")
    private String applyUserName;

    /**
     * 流程发起时间
     */
    @JsonProperty("af_applytime")
    private Date applyTime;

    /**
     * 流程是否已结束
     */
    @JsonIgnore
    private boolean hasEnd;

    /**
     * 节点是否是退回来的
     */
    @JsonProperty("af_isfallback")
    private boolean fallBack;

    /**
     * 流程状态
     */
    @JsonIgnore
    private ProcessStatus status;

    /**
     * 流程状态名
     */
    @JsonProperty("af_statusname")
    private String statusName;

    /**
     * 流程结束的原因（正常结束还是终止结束）
     */
    @JsonIgnore
    private String deleteReason;

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

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getApplyUserCode() {
        return applyUserCode;
    }

    public void setApplyUserCode(String applyUserCode) {
        this.applyUserCode = applyUserCode;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public String getStatusName() {
        return this.status.getStatusName();
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
    }

    public Boolean getHasAssigned() {
        return hasAssigned;
    }

    public void setHasAssigned(Boolean hasAssigned) {
        this.hasAssigned = hasAssigned;
    }

    public boolean isFallBack() {
        return fallBack;
    }

    public void setFallBack(boolean fallBack) {
        this.fallBack = fallBack;
    }

    public boolean isHasEnd() {
        return hasEnd;
    }

    public void setHasEnd(boolean hasEnd) {
        this.hasEnd = hasEnd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }
}
