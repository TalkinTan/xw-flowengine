package com.xuanwu.flowengine.cmd;

import com.xuanwu.flowengine.context.ProcessContext;
import com.xuanwu.flowengine.dao.TaskRouteDao;
import com.xuanwu.flowengine.dao.impl.TaskRouteDaoImpl;
import com.xuanwu.flowengine.entity.TaskRouteEntity;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;

/**
 * 保存自定义的任务流转记录命令类
 * File created on 2017/6/13.
 *
 * @author jkun
 */
public class SaveTaskRouteCmd implements Command<Boolean> {

    private TaskRouteEntity entity;

    private TaskRouteDao dao = new TaskRouteDaoImpl();

    public SaveTaskRouteCmd(TaskRouteEntity entity) {
        this.entity = entity;
    }

    @Override
    public Boolean execute(CommandContext commandContext) {
        if (entity.getRouteId() == null) {
            entity.setRouteId(Long.valueOf(ProcessContext.getIdGenerator().getNextId()));
            return dao.insertTaskRoute(entity);
        } else {
            return dao.updateTaskRoute(entity);
        }
    }
}
