package com.xuanwu.flowengine.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 自定义任务流转记录实体类
 * File created on 2017/6/7.
 *
 * @author jkun
 */
public class TaskRouteEntity implements Serializable {

    private static final long serialVersionUID = 1212121l;

    /**
     * 用户任务route记录Id
     */
    private Long routeId;

    /**
     * 流程实例Id
     */
    private String processInstanceId;

    /**
     * 流程执行时Id
     */
    private String processExecutionId;

    /**
     * 流程定义Id
     */
    private String processDefineId;

    /**
     * 用户任务实例Id
     */
    private String taskInstanceId;

    /**
     * 用户任务Key
     */
    private String taskKey;

    /**
     * 用户任务名称
     */
    private String taskName;

    /**
     * 用户任务开始时间
     */
    private Date startTime;

    /**
     * 用户任务结束时间
     */
    private Date endTime;

    /**
     * 用户任务进入类型
     */
    private String jumpInType;

    /**
     * 用户任务跳出类型
     */
    private String jumpOutType;

    /**
     * 前置用户任务Route记录Id
     */
    private Long prevRouteId;

    /**
     * 是否为会审任务
     */
    private boolean jointNode;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessExecutionId() {
        return processExecutionId;
    }

    public void setProcessExecutionId(String processExecutionId) {
        this.processExecutionId = processExecutionId;
    }

    public String getProcessDefineId() {
        return processDefineId;
    }

    public void setProcessDefineId(String processDefineId) {
        this.processDefineId = processDefineId;
    }

    public String getTaskInstanceId() {
        return taskInstanceId;
    }

    public void setTaskInstanceId(String taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getJumpInType() {
        return jumpInType;
    }

    public void setJumpInType(String jumpInType) {
        this.jumpInType = jumpInType;
    }

    public String getJumpOutType() {
        return jumpOutType;
    }

    public void setJumpOutType(String jumpOutType) {
        this.jumpOutType = jumpOutType;
    }

    public Long getPrevRouteId() {
        return prevRouteId;
    }

    public void setPrevRouteId(Long prevRouteId) {
        this.prevRouteId = prevRouteId;
    }

    public boolean isJointNode() {
        return jointNode;
    }

    public void setJointNode(boolean jointNode) {
        this.jointNode = jointNode;
    }
}
