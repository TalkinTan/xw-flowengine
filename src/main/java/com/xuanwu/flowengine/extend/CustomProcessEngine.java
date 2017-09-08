package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.service.FlowCategoryService;
import com.xuanwu.flowengine.service.FlowRelationService;
import com.xuanwu.flowengine.service.ProcessCoreService;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;

/**
 * 自定义流程引擎类
 * Created by jkun on 2017/3/15.
 *
 * @author jkun
 */
public class CustomProcessEngine extends ProcessEngineImpl {
    protected FlowCategoryService flowCategoryService;
    protected FlowRelationService flowRelationService;
    protected ProcessCoreService processCoreService;

    public CustomProcessEngine(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
        this.flowCategoryService = ((CustomMultiTenantProcessEngineConfiguration) processEngineConfiguration).getFlowCategoryService();
        this.flowRelationService = ((CustomMultiTenantProcessEngineConfiguration) processEngineConfiguration).getFlowRelationService();
        this.processCoreService = new ProcessCoreService(processEngineConfiguration);
    }

    public FlowCategoryService getFlowCategoryService() {
        return flowCategoryService;
    }

    public FlowRelationService getFlowRelationService() {
        return flowRelationService;
    }

    public ProcessCoreService getProcessCoreService() {
        return processCoreService;
    }
}
