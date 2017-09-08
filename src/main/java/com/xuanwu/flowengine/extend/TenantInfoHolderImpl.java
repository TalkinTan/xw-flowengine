package com.xuanwu.flowengine.extend;

import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 线程租户信息变量容器
 * File created on 2017/4/21.
 *
 * @author jkun
 */
public class TenantInfoHolderImpl implements TenantInfoHolder {
    protected static ThreadLocal<String> tenantKey = new ThreadLocal<String>();
    protected static Set<String> tenants = new HashSet<>();

    @Override
    public Collection<String> getAllTenants() {
        return tenants;
    }

    @Override
    public void setCurrentTenantId(String tenantid) {
        tenantKey.set(tenantid);
        tenants.add(tenantid);
    }

    @Override
    public String getCurrentTenantId() {
        return tenantKey.get();
    }

    @Override
    public void clearCurrentTenantId() {
        String tenantid = tenantKey.get();
        tenantKey.set(null);
        tenants.remove(tenantid);
    }
}
