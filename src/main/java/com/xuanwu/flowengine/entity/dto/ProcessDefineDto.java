package com.xuanwu.flowengine.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xuanwu.flowengine.entity.ProcessDefineWrapper;

/**
 * 流程定义Dto
 * Created by jkun on 2017/3/20.
 *
 * @author jkun
 */
public class ProcessDefineDto extends ProcessDefineWrapper {
    /**
     * 流程定义xml
     */
    @JsonProperty("af_resource")
    private String resource;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
