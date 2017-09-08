package com.xuanwu.flowengine.factory;

import com.xuanwu.flowengine.constant.DataBaseType;
import com.xuanwu.flowengine.context.ProcessContext;
import com.xuanwu.flowengine.extend.CustomMultiTenantProcessEngineConfiguration;
import com.xuanwu.flowengine.util.DataSourceRouterUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.parse.BpmnParseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 多租户流程引擎工厂对象
 * File created on 2017/4/21.
 *
 * @author jkun
 */
public class MultiTenantProcessEngineFactory {
    private static Logger logger = LoggerFactory.getLogger(MultiTenantProcessEngineFactory.class);

    private static CustomMultiTenantProcessEngineConfiguration configuration;
    private static volatile ProcessEngine processEngine;
    private static Object lock = new Object();

    static {
        configuration = new CustomMultiTenantProcessEngineConfiguration(ProcessContext.getTenantInfoHolder());
        configuration.setDatabaseType(MultiSchemaMultiTenantProcessEngineConfiguration.DATABASE_TYPE_POSTGRES);

        //设置自定义的扩展解析器
        List<BpmnParseHandler> parseHandlers = new ArrayList<>();
        parseHandlers.add(ProcessContext.getProcessParseHandler());
        parseHandlers.add(ProcessContext.getStartEventParseHandler());
        parseHandlers.add(ProcessContext.getUserTaskParseHandler());
        configuration.setCustomDefaultBpmnParseHandlers(parseHandlers);

        //设置流程图片生成的字体
        configuration.setActivityFontName("黑体");
        configuration.setLabelFontName("黑体");
        configuration.setAnnotationFontName("黑体");

        //设置事务管理器
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(configuration.getDataSource());
        configuration.setTransactionManager(transactionManager);

        //设置扩展的Id生成器
        configuration.setIdGenerator(ProcessContext.getIdGenerator());

        //替换默认的用户、组服务
        List<SessionFactory> customerSessionFactories = new ArrayList<>();
        customerSessionFactories.add(ProcessContext.getCustomGroupEntityManagerFactory());
        customerSessionFactories.add(ProcessContext.getCustomUserEntityManagerFactory());
        configuration.setCustomSessionFactories(customerSessionFactories);

        // 设置全局事件监听器
        configuration.setTypedEventListeners(ProcessContext.getCustomEventListener());
    }

    public static ProcessEngine getProcessEngine(Long tenantCode, Long productCode, Long workerId, Long dataCenterId) throws Exception {
        // 获取数据源
        DataSource dataSource = DataSourceRouterUtil.getDataSource(tenantCode, DataBaseType.READ_AND_WRITE, productCode);
        if (null == dataSource) {
            logger.error(String.format("get tenant database connection fail, tenantCode: %d, productCode: %d", tenantCode, productCode));
            return null;
        }

        String combineKey = String.format("%s|%s", tenantCode, productCode);
        synchronized (lock) {
            // lock to avoid the dataSource hashMap thread unsafe problem and keep the processEngine unique
            configuration.registerTenant(combineKey, dataSource);
            if (null == processEngine) {
                processEngine = configuration.buildProcessEngine();
            }
        }

        ProcessContext.setCurrentTenantIdentity(tenantCode, productCode, workerId, dataCenterId);
        return processEngine;
    }
}
