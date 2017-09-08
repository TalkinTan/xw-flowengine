package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.context.ProcessContext;
import com.xuanwu.flowengine.entity.TenantIdentity;
import com.xuanwu.flowengine.util.IdWorker;
import org.activiti.engine.impl.cfg.IdGenerator;

/**
 * 分布式全局唯一Id生成器
 * File created on 2017/4/24.
 *
 * @author jkun
 */
public class UniqueIdGenerator implements IdGenerator {

    @Override
    public String getNextId() {
        // 从当前线程获取租户相关标识
        TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
        IdWorker idWorker = new IdWorker(identity.getWorkId(), identity.getDataCenterId());
        String id = String.valueOf(idWorker.genId());
        return id;
    }
}
