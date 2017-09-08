package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 流程追踪对象
 * Created by jkun on 2017/2/14.
 *
 * @author jkun
 */
public class ProcessTraceInfo {
    /**
     * 活动Id
     */
    @JsonProperty("af_actid")
    private String actId;

    /**
     * 活动名
     */
    @JsonProperty("af_actname")
    private String actName;

    /**
     * 活动类型
     */
    @JsonProperty("af_acttype")
    private String actType;

    /**
     * 处理人编码
     */
    @JsonProperty("af_assigneecode")
    private String assigneeCode;

    /**
     * 处理人姓名
     */
    @JsonProperty("af_assignee")
    private String assignee;

    /**
     * 处理完成时间
     */
    @JsonProperty("af_handletime")
    private String handleTime;

    /**
     * 审批意见
     */
    @JsonProperty("af_comments")
    private String comments;

    /**
     * 审批选择Code
     */
    @JsonProperty("af_choice")
    private Integer choice;

    /**
     * 审批选择名称(同意、不同意....)
     */
    @JsonProperty("af_choicename")
    private String choiceName;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getAssigneeCode() {
        return assigneeCode;
    }

    public void setAssigneeCode(String assigneeCode) {
        this.assigneeCode = assigneeCode;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }
}
