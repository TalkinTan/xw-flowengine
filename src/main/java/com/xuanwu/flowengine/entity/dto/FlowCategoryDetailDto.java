package com.xuanwu.flowengine.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xuanwu.flowengine.entity.FlowCategoryEntity;

import java.util.List;

/**
 * File created on 2017/5/25.
 *
 * @author jkun
 */
public class FlowCategoryDetailDto extends FlowCategoryEntity {
    @JsonProperty("af_processlist")
    private List<ProcessBasicInfo> processList;
}

/**
 * 流程定义基础信息
 */
class ProcessBasicInfo {
    /**
     * 流程定义Key
     */
    @JsonProperty("af_processkey")
    private String processKey;

    /**
     * 流程定义名称
     */
    @JsonProperty("af_processname")
    private String processName;

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}

