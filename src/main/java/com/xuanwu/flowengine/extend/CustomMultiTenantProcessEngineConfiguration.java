package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.service.FlowCategoryService;
import com.xuanwu.flowengine.service.FlowRelationService;
import com.xuanwu.flowengine.service.TaskRouteService;
import com.xuanwu.flowengine.service.impl.FlowCategoryServiceImpl;
import com.xuanwu.flowengine.service.impl.FlowRelationServiceImpl;
import com.xuanwu.flowengine.service.impl.TaskRouteServiceImpl;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandInterceptor;
import org.activiti.engine.impl.persistence.deploy.MultiSchemaMultiTenantProcessDefinitionCache;
import org.activiti.spring.SpringTransactionContextFactory;
import org.activiti.spring.SpringTransactionInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.InputStream;

/**
 * File created on 2017/4/24.
 *
 * @author jkun
 */
public class CustomMultiTenantProcessEngineConfiguration extends MultiSchemaMultiTenantProcessEngineConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CustomMultiTenantProcessEngineConfiguration.class);
    private PlatformTransactionManager transactionManager;
    protected Integer transactionSynchronizationAdapterOrder = null;

    public static final String CUSTOM_MYBATIS_MAPPING_FILE = "com/xuanwu/apaas/db/mapping/mappings.xml";
    protected FlowCategoryService flowCategoryService = new FlowCategoryServiceImpl();
    protected FlowRelationService flowRelationService = new FlowRelationServiceImpl();
    protected TaskRouteService taskRouteService = new TaskRouteServiceImpl();

    public CustomMultiTenantProcessEngineConfiguration(TenantInfoHolder tenantInfoHolder) {
        super(tenantInfoHolder);
        this.transactionsExternallyManaged = true;
    }

    @Override
    protected void initDefaultCommandConfig() {
        if (defaultCommandConfig == null) {
            defaultCommandConfig = new CommandConfig().setContextReusePossible(true);
        }
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        if (databaseType == null) {
            throw new ActivitiException("Setting the databaseType is mandatory when using MultiSchemaMultiTenantProcessEngineConfiguration");
        }

        // Disable schema creation/validation by setting it to null.
        // We'll do it manually, see buildProcessEngine() method (hence why it's copied first)
        String originalDatabaseSchemaUpdate = this.databaseSchemaUpdate;
        this.databaseSchemaUpdate = null;

        // Using a cache / tenant to avoid process definition id conflicts
        this.processDefinitionCache = new MultiSchemaMultiTenantProcessDefinitionCache(tenantInfoHolder, this.processDefinitionCacheLimit);

        // Also, we shouldn't start the async executor until *after* the schema's have been created
        boolean originalIsAutoActivateAsyncExecutor = this.asyncExecutorActivate;
        this.asyncExecutorActivate = false;

        // 获取流程引擎对象
        init();
        ProcessEngine processEngine = new CustomProcessEngine(this);
        ProcessEngines.setInitialized(true);

        // Reset to original values
        this.databaseSchemaUpdate = originalDatabaseSchemaUpdate;
        this.asyncExecutorActivate = originalIsAutoActivateAsyncExecutor;

        // Create tenant schema
        for (String tenantId : tenantInfoHolder.getAllTenants()) {
            createTenantSchema(tenantId);
        }

        // Start async executor
        if (asyncExecutor != null && originalIsAutoActivateAsyncExecutor) {
            asyncExecutor.start();
        }

        booted = true;
        return processEngine;
    }

    /**
     * 重写获取mybatis配置资源
     *
     * @return
     */
    @Override
    protected InputStream getMyBatisXmlConfigurationSteam() {
        return getResourceAsStream(CUSTOM_MYBATIS_MAPPING_FILE);
    }

    @Override
    protected void initServices() {
        super.initServices();
        initService(flowCategoryService);
        initService(flowRelationService);
        initService(taskRouteService);
    }

    /**
     * 事务拦截器
     *
     * @return
     */
    @Override
    protected CommandInterceptor createTransactionInterceptor() {
        if (transactionManager == null) {
            throw new ActivitiException("transactionManager is required property for SpringProcessEngineConfiguration, use "
                    + StandaloneProcessEngineConfiguration.class.getName() + " otherwise");
        }

        return new SpringTransactionInterceptor(transactionManager);
    }

    @Override
    protected void initTransactionContextFactory() {
        if (transactionContextFactory == null && transactionManager != null) {
            transactionContextFactory = new SpringTransactionContextFactory(transactionManager, transactionSynchronizationAdapterOrder);
        }
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * 设置事务管理器
     *
     * @param transactionManager
     */
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setTransactionSynchronizationAdapterOrder(Integer transactionSynchronizationAdapterOrder) {
        this.transactionSynchronizationAdapterOrder = transactionSynchronizationAdapterOrder;
    }

    public FlowCategoryService getFlowCategoryService() {
        return flowCategoryService;
    }

    public FlowRelationService getFlowRelationService() {
        return flowRelationService;
    }

    public TaskRouteService getTaskRouteService() {
        return taskRouteService;
    }

    /**
     * 暴露出tenantInfoHolder对象出来
     *
     * @return
     */
    public TenantInfoHolder getTenantInfoHolder() {
        return this.tenantInfoHolder;
    }
}
