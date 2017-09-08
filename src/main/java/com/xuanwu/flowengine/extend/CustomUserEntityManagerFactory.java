package com.xuanwu.flowengine.extend;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;

/**
 * 扩展用户管理器工厂
 * Created by jkun on 2017/2/15.
 * @author jkun
 */
public class CustomUserEntityManagerFactory implements SessionFactory {

    private CustomUserEntityManager userEntityManager;

    public void setUserEntityManager(CustomUserEntityManager userEntityManager) {
        this.userEntityManager = userEntityManager;
    }

    @Override
    public Class<?> getSessionType() {
        return UserIdentityManager.class;
    }

    @Override
    public Session openSession() {
        return this.userEntityManager;
    }
}
