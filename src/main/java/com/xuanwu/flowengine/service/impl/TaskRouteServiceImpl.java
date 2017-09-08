package com.xuanwu.flowengine.service.impl;

import com.xuanwu.flowengine.cmd.SaveTaskRouteCmd;
import com.xuanwu.flowengine.cmd.TaskRouteQueryImpl;
import com.xuanwu.flowengine.entity.TaskRouteEntity;
import com.xuanwu.flowengine.service.TaskRouteService;
import org.activiti.engine.impl.ServiceImpl;

/**
 * 自定义任务流转记录服务实现层
 * File created on 2017/6/13.
 *
 * @author jkun
 */
public class TaskRouteServiceImpl extends ServiceImpl implements TaskRouteService {
    @Override
    public boolean insertTaskRoute(TaskRouteEntity entity) {
        return commandExecutor.execute(new SaveTaskRouteCmd(entity));
    }

    @Override
    public boolean updateTaskRoute(TaskRouteEntity entity) {
        return commandExecutor.execute(new SaveTaskRouteCmd(entity));
    }

    @Override
    public TaskRouteEntity findTaskRouteById(String taskId) {
        TaskRouteQueryImpl query = new TaskRouteQueryImpl(commandExecutor);
        TaskRouteEntity result = query.taskId(taskId).singleResult();
        return result;
    }
}
