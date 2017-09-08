package com.xuanwu.flowengine.factory;

import com.xuanwu.flowengine.constant.DataBaseType;
import com.xuanwu.flowengine.extend.*;
import com.xuanwu.flowengine.util.DataSourceRouterUtil;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.parse.BpmnParseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.*;

/**
 * 流程引擎对象创建工厂
 * Created by jkun on 2017/2/7.
 *
 * @author jkun
 */
public class ProcessEngineFactory {

    private static final Logger logger = LoggerFactory.getLogger(ProcessEngineFactory.class);

    private static Map<String, ProcessEngine> processEngines = new HashMap<String, ProcessEngine>();

    private ProcessEngineFactory() {
    }

    /**
     * 根据租户编码和产品编码定位租户数据库，实例化得到流程引擎对象
     *
     * @param tenantCode   租户编码
     * @param productCode  产品编码
     * @param workerId
     * @param dataCenterId
     * @return 流程引擎对象
     */
    public static synchronized ProcessEngine getProcessEngine(Long tenantCode, Long productCode, Long workerId, Long dataCenterId) throws Exception {
        StringBuilder sb = new StringBuilder(String.valueOf(tenantCode));
        sb.append(productCode);

        ProcessEngine engine = processEngines.get(sb.toString());
        if (null == engine) {
            DataSource dataSource = DataSourceRouterUtil.getDataSource(tenantCode, DataBaseType.READ_AND_WRITE, productCode);
            if (null == dataSource) {
                logger.error("get tenant database connection fail, tenantCode: %d, productCode: %d", tenantCode, productCode);
                return null;
            }

            ProcessEngineConfiguration configuration = initProcessEngineConfiguration(dataSource, tenantCode, productCode, workerId, dataCenterId);
            engine = configuration.buildProcessEngine();
            if (null != engine) {
                processEngines.put(sb.toString(), engine);
            }
        }

        return engine;
    }

    /**
     * 根据数据源初始化流程引擎配置对象
     *
     * @param dataSource
     * @return
     */
    private static ProcessEngineConfiguration initProcessEngineConfiguration(DataSource dataSource, Long tenantCode, Long productCode, Long workerId, Long dataCenterId) {
        CustomProcessEngineConfiguration configuration = new CustomProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        //configuration.setDatabaseSchemaUpdate("drop-create");
        //替换用户、组服务
        List<SessionFactory> customerSessionFactories = new ArrayList<>();
        CustomGroupEntityManagerFactory groupFactory = new CustomGroupEntityManagerFactory();
        //groupFactory.setGroupEntityManager(new CustomGroupEntityManager(tenantCode, productCode));
        CustomUserEntityManagerFactory userFactory = new CustomUserEntityManagerFactory();
        //userFactory.setUserEntityManager(new CustomUserEntityManager(tenantCode, productCode));
        customerSessionFactories.add(groupFactory);
        customerSessionFactories.add(userFactory);
        configuration.setCustomSessionFactories(customerSessionFactories);

        //设置自定义的扩展解析器
        List<BpmnParseHandler> parseHandlers = new ArrayList<>();
        parseHandlers.add(new ExtensionStartEventParseHandler());
        parseHandlers.add(new ExtensionUserTaskParseHandler());
        configuration.setCustomDefaultBpmnParseHandlers(parseHandlers);

        //设置事务管理器
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        configuration.setTransactionManager(transactionManager);

        //设置自定义的IdGenerator
        CustomIdGenerator idGenerator = new CustomIdGenerator(workerId, dataCenterId);
        configuration.setIdGenerator(idGenerator);

        //设置流程图片生成的字体
        configuration.setActivityFontName("黑体");
        configuration.setLabelFontName("黑体");
        configuration.setAnnotationFontName("黑体");

        /*// 替换ActivityBehaviorFactory类
        CustomActivityBehaviorFactory activityBehaviorFactory = new CustomActivityBehaviorFactory();
        configuration.setActivityBehaviorFactory(activityBehaviorFactory);*/

        return configuration;
    }

}
