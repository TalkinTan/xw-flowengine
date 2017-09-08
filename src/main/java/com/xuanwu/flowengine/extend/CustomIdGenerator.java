package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.util.IdWorker;
import org.activiti.engine.impl.cfg.IdGenerator;

/**
 * 自定义ID生成器，用来替换Activiti自带的步长Id生成器
 * File created on 2017/3/25.
 *
 * @author jkun
 */
public class CustomIdGenerator implements IdGenerator {

    /**
     * 用于生成唯一Id
     */
    private IdWorker idWorker;

    public CustomIdGenerator(Long workerId, Long dataCenterId) {
        this.idWorker = new IdWorker(workerId, dataCenterId);
    }

    @Override
    public String getNextId() {
        String id = String.valueOf(idWorker.genId());
        return id;
    }
}
