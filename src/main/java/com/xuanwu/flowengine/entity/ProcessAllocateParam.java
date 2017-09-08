package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 流程发起入参对象
 * File created on 2017/6/5.
 *
 * @author jkun
 */
public class ProcessAllocateParam {

    /**
     * 流程定义Key
     */
    @JsonProperty("af_processdefinekey")
    private String processDefineKey;

    /**
     * 流程实例名称
     */
    @JsonProperty("af_processinstancename")
    private String processInstanceName;

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

    public String getProcessDefineKey() {
        return processDefineKey;
    }

    public void setProcessDefineKey(String processDefineKey) {
        this.processDefineKey = processDefineKey;
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
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
}
