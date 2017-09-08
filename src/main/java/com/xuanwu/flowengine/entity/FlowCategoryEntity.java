package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 流程分类定义实体
 * Created by jkun on 2017/3/14.
 *
 * @author jkun
 */
public class FlowCategoryEntity {

    /**
     * 流程分类编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("af_flowcategorycode")
    private Long flowCategoryCode;

    /**
     * 流程分类名称
     */
    @JsonProperty("af_flowcategoryname")
    private String flowCategoryName;

    /**
     * 流程分类父编码
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("af_parentcode")
    private Long parentCode;

    /**
     * 状态
     */
    @JsonProperty("af_status")
    private int status;

    public Long getFlowCategoryCode() {
        return flowCategoryCode;
    }

    public void setFlowCategoryCode(Long flowCategoryCode) {
        this.flowCategoryCode = flowCategoryCode;
    }

    public String getFlowCategoryName() {
        return flowCategoryName;
    }

    public void setFlowCategoryName(String flowCategoryName) {
        this.flowCategoryName = flowCategoryName;
    }

    public Long getParentCode() {
        return parentCode;
    }

    public void setParentCode(Long parentCode) {
        this.parentCode = parentCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
