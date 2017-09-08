package com.xuanwu.flowengine.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * File created on 2017/8/7.
 *
 * @author jkun
 */
public class TaskBasic {
    /**
     * 用户任务实例Id
     */
    @JsonProperty("af_taskid")
    private String taskId;

    /**
     * 用户任务Key
     */
    @JsonProperty("af_taskkey")
    private String taskKey;

    /**
     * 用户任务名称
     */
    @JsonProperty("af_taskname")
    private String taskName;

    /**
     * 用户任务实例处理人
     */
    @JsonProperty("af_handler")
    private String handler;

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

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
