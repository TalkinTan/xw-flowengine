package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.service.FlowCategoryService;
import com.xuanwu.flowengine.service.FlowRelationService;
import com.xuanwu.flowengine.service.impl.FlowCategoryServiceImpl;
import com.xuanwu.flowengine.service.impl.FlowRelationServiceImpl;
import org.activiti.engine.ProcessEngine;
import org.activiti.spring.SpringProcessEngineConfiguration;

import java.io.InputStream;

/**
 * 自定义流程配置类
 * Created by jkun on 2017/3/15.
 *
 * @author jkun
 */
public class CustomProcessEngineConfiguration extends SpringProcessEngineConfiguration {

    public static final String CUSTOM_MYBATIS_MAPPING_FILE = "com/xuanwu/apaas/db/mapping/mappings.xml";

    protected FlowCategoryService flowCategoryService = new FlowCategoryServiceImpl();
    protected FlowRelationService flowRelationService = new FlowRelationServiceImpl();

    @Override
    protected InputStream getMyBatisXmlConfigurationSteam() {
        return getResourceAsStream(CUSTOM_MYBATIS_MAPPING_FILE);
    }

    @Override
    protected void initServices() {
        super.initServices();
        initService(flowCategoryService);
        initService(flowRelationService);
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        init();
        return new CustomProcessEngine(this);
    }

    public FlowCategoryService getFlowCategoryService() {
        return flowCategoryService;
    }

    public FlowRelationService getFlowRelationService() {
        return flowRelationService;
    }
}
