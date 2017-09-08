package com.xuanwu.flowengine.context;

import com.xuanwu.flowengine.entity.TenantIdentity;
import com.xuanwu.flowengine.listener.GlobalEventListener;
import com.xuanwu.flowengine.service.MemberQueryService;
import com.xuanwu.flowengine.service.impl.MemberQueryServiceImpl;
import com.xuanwu.flowengine.extend.*;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义流程引擎上下文
 * File created on 2017/5/25.
 *
 * @author jkun
 */
public class ProcessContext {

    protected static ThreadLocal<TenantIdentity> tenantIdentityThreadLocal = new ThreadLocal<>();
    protected static TenantInfoHolder tenantInfoHolder = new TenantInfoHolderImpl();

    protected static ExtensionStartEventParseHandler startEventParseHandler = new ExtensionStartEventParseHandler();
    protected static ExtensionUserTaskParseHandler userTaskParseHandler = new ExtensionUserTaskParseHandler();
    protected static ExtensionProcessParseHandler processParseHandler = new ExtensionProcessParseHandler();

    protected static MemberQueryService memberQueryService;
    protected static CustomGroupEntityManagerFactory customGroupEntityManagerFactory;
    protected static CustomUserEntityManagerFactory customUserEntityManagerFactory;

    protected static Map<String, List<ActivitiEventListener>> customEventListener;

    protected static IdGenerator idGenerator = new UniqueIdGenerator();

    static {
        memberQueryService = new MemberQueryServiceImpl();
        customGroupEntityManagerFactory = new CustomGroupEntityManagerFactory();
        customGroupEntityManagerFactory.setGroupEntityManager(new CustomGroupEntityManager(memberQueryService));
        customUserEntityManagerFactory = new CustomUserEntityManagerFactory();
        customUserEntityManagerFactory.setUserEntityManager(new CustomUserEntityManager(memberQueryService));

        customEventListener = new HashMap<>();
        GlobalEventListener globalEventListener = new GlobalEventListener();
        customEventListener.put("TASK_CREATED,TASK_COMPLETED", Arrays.asList(globalEventListener));
    }

    public static TenantInfoHolder getTenantInfoHolder() {
        return tenantInfoHolder;
    }

    public static ExtensionStartEventParseHandler getStartEventParseHandler() {
        return startEventParseHandler;
    }

    public static ExtensionUserTaskParseHandler getUserTaskParseHandler() {
        return userTaskParseHandler;
    }

    public static ExtensionProcessParseHandler getProcessParseHandler() {
        return processParseHandler;
    }

    public static MemberQueryService getMemberQueryService() {
        return memberQueryService;
    }

    public static CustomGroupEntityManagerFactory getCustomGroupEntityManagerFactory() {
        return customGroupEntityManagerFactory;
    }

    public static CustomUserEntityManagerFactory getCustomUserEntityManagerFactory() {
        return customUserEntityManagerFactory;
    }

    public static TenantIdentity getCurrentTenantIdentity() {
        TenantIdentity identity = tenantIdentityThreadLocal.get();
        if (null == identity) {
            throw new ActivitiException("can not find the current thread tenant identity information!");
        }

        return identity;
    }

    public static void setCurrentTenantIdentity(Long tenantCode, Long productCode, Long workId, Long dataCenterId) {
        TenantIdentity identity = new TenantIdentity(tenantCode, productCode, workId, dataCenterId);
        String combineKey = String.format("%s|%s", tenantCode, productCode);
        tenantInfoHolder.setCurrentTenantId(combineKey);
        tenantIdentityThreadLocal.set(identity);
    }

    public static IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public static Map<String, List<ActivitiEventListener>> getCustomEventListener() {
        return customEventListener;
    }
}
