package com.xuanwu.flowengine.extend;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;

/**
 * 扩展Activiti用户组管理器工厂
 * Created by jkun on 2017/2/16.
 * @author jkun
 */
public class CustomGroupEntityManagerFactory implements SessionFactory {

    private CustomGroupEntityManager groupEntityManager;

    public void setGroupEntityManager(CustomGroupEntityManager groupEntityManager) {
        this.groupEntityManager = groupEntityManager;
    }

    @Override
    public Class<?> getSessionType() {
        return GroupIdentityManager.class;
    }

    @Override
    public Session openSession() {
        return this.groupEntityManager;
    }
}
