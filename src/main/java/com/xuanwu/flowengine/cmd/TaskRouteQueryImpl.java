package com.xuanwu.flowengine.cmd;

import com.xuanwu.flowengine.dao.TaskRouteDao;
import com.xuanwu.flowengine.dao.impl.TaskRouteDaoImpl;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.TaskRouteEntity;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义的任务流转记录查询接口类
 * File created on 2017/6/13.
 *
 * @author jkun
 */
public class TaskRouteQueryImpl extends AbstractQuery<TaskRouteEntity> {

    private String taskId;
    private TaskRouteDao dao = new TaskRouteDaoImpl();

    public TaskRouteQueryImpl taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public TaskRouteQueryImpl(CommandExecutor executor) {
        super(executor);
    }

    @Override
    public List<TaskRouteEntity> executeList(CommandContext commandContext) {
        TaskRouteEntity entity = dao.findTaskRouteEntityById(taskId);
        return Arrays.asList(entity);
    }

    @Override
    public Page<TaskRouteEntity> executePage(CommandContext commandContext) {
        return null;
    }
}
