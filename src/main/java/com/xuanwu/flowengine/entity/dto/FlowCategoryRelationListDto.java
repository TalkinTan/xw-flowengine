package com.xuanwu.flowengine.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xuanwu.flowengine.entity.FlowCategoryRelationEntity;

/**
 * 流程关联关系Dto
 * Created by jkun on 2017/3/17.
 *
 * @author jkun
 */
public class FlowCategoryRelationListDto extends FlowCategoryRelationEntity {
    /**
     * 流程分类名
     */
    @JsonProperty("af_flowcategoryname")
    private String flowCategoryName;

    /**
     * 流程部署Id
     */
    @JsonProperty("af_processdeployid")
    private String processDeployId;

    /**
     * 流程定义Id
     */
    @JsonProperty("af_processdefineid")
    private String processDefineId;

    /**
     * 修改标志
     */
    @JsonProperty("af_changeflag")
    private boolean changeFlag;

    /**
     * 流程停用、启用状态
     */
    @JsonProperty("af_suspendstate")
    private int suspendState;

    public String getFlowCategoryName() {
        return flowCategoryName;
    }

    public void setFlowCategoryName(String flowCategoryName) {
        this.flowCategoryName = flowCategoryName;
    }

    public String getProcessDeployId() {
        return processDeployId;
    }

    public void setProcessDeployId(String processDeployId) {
        this.processDeployId = processDeployId;
    }

    public String getProcessDefineId() {
        return processDefineId;
    }

    public void setProcessDefineId(String processDefineId) {
        this.processDefineId = processDefineId;
    }

    public int getSuspendState() {
        return suspendState;
    }

    public void setSuspendState(int suspendState) {
        this.suspendState = suspendState;
    }

    public boolean isChangeFlag() {
        return changeFlag;
    }

    public void setChangeFlag(boolean changeFlag) {
        this.changeFlag = changeFlag;
    }
}
