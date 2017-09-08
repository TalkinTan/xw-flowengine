package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

/**
 * 流程分类关联实体
 * Created by Administrator on 2017/3/14.
 */
public class FlowCategoryRelationEntity {

    public FlowCategoryRelationEntity() {

    }

    public FlowCategoryRelationEntity(Long directoryCode, Long flowCategoryCode) {
        this.directoryCode = directoryCode;
        this.flowCategoryCode = flowCategoryCode;
    }

    /**
     * 流程定义编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("af_flowrelationcode")
    private Long flowRelationCode;

    /**
     * 流程模型编码
     */
    @JsonProperty("af_flowmodelcode")
    private String flowModelCode;

    /**
     * 流程模型名称
     */
    @JsonProperty("af_flowmodelname")
    private String flowModelName;

    /**
     * 流程所在目录编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("af_directorycode")
    private Long directoryCode;

    /**
     * 流程分类编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("af_flowcategorycode")
    private Long flowCategoryCode;

    /**
     * 创建时间
     */
    @JsonProperty("af_createtime")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonProperty("af_updateTime")
    private Date updateTime;

    /**
     * 状态
     */
    @JsonProperty("af_status")
    private int status;

    public Long getFlowRelationCode() {
        return flowRelationCode;
    }

    public void setFlowRelationCode(Long flowRelationCode) {
        this.flowRelationCode = flowRelationCode;
    }

    public String getFlowModelCode() {
        return flowModelCode;
    }

    public void setFlowModelCode(String flowModelCode) {
        this.flowModelCode = flowModelCode;
    }

    public String getFlowModelName() {
        return flowModelName;
    }

    public void setFlowModelName(String flowModelName) {
        this.flowModelName = flowModelName;
    }

    public Long getDirectoryCode() {
        return directoryCode;
    }

    public void setDirectoryCode(Long directoryCode) {
        this.directoryCode = directoryCode;
    }

    public Long getFlowCategoryCode() {
        return flowCategoryCode;
    }

    public void setFlowCategoryCode(Long flowCategoryCode) {
        this.flowCategoryCode = flowCategoryCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
