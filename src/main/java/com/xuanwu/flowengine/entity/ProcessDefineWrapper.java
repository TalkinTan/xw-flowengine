package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 流程定义包装对象
 * Created by jkun on 2017/2/14.
 *
 * @author jkun
 */
public class ProcessDefineWrapper {
    /**
     * 流程定义Id
     */
    @JsonProperty("af_processdefineid")
    private String processDefineId;

    /**
     * 流程定义Key (一般用于发起流程)
     */
    @JsonProperty("af_processdefinekey")
    private String processDefineKey;

    /**
     * 流程定义名称
     */
    @JsonProperty("af_processdefinename")
    private String processDefineName;

    /**
     * 流程发起UI配置信息
     */
    @JsonProperty("af_uiconfig")
    private JsonNode uiConfig;

    /**
     * 流程实例名生成规则
     */
    @JsonProperty("af_instancenamerule")
    private String instanceNameRule;

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

    public JsonNode getUiConfig() {
        return uiConfig;
    }

    public void setUiConfig(JsonNode uiConfig) {
        this.uiConfig = uiConfig;
    }

    public String getInstanceNameRule() {
        return instanceNameRule;
    }

    public void setInstanceNameRule(String instanceNameRule) {
        this.instanceNameRule = instanceNameRule;
    }
}
