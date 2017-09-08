package com.xuanwu.flowengine.factory;

import com.xuanwu.flowengine.service.WorkflowService;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;

/**
 * 流程服务创建类,供外部调用
 * Created by jkun on 2017/2/20.
 *
 * @author jkun
 */
public class WorkflowServiceFactory {

    private WorkflowServiceFactory() {
    }

    /**
     * @param tenantCode   租户编码
     * @param productCode  产品编码
     * @param workerId
     * @param dataCenterId
     * @return 流程服务对象
     * @throws Exception
     */
    public static WorkflowService getWorkflowService(Long tenantCode, Long productCode, Long workerId, Long dataCenterId) throws Exception {
        ProcessEngine processEngine = MultiTenantProcessEngineFactory.getProcessEngine(tenantCode, productCode, workerId, dataCenterId);

        if (null == processEngine) {
            throw new ActivitiException("can not init process engine!");
        }

        WorkflowService workflowService = new WorkflowService(processEngine);
        return workflowService;
    }
}
