package com.xuanwu.flowengine.dao.impl;

import com.xuanwu.flowengine.dao.TaskRouteDao;
import com.xuanwu.flowengine.entity.TaskRouteEntity;
import com.xuanwu.flowengine.util.ParamMap;
import org.activiti.engine.impl.persistence.AbstractManager;

/**
 * 自定义任务流转记录数据访问实现层
 * File created on 2017/6/13.
 *
 * @author jkun
 */
public class TaskRouteDaoImpl extends AbstractManager implements TaskRouteDao {
    @Override
    public boolean insertTaskRoute(TaskRouteEntity entity) {
        int influenceRows = getDbSqlSession().getSqlSession().insert(this.getClass().getName() + ".insert", entity);
        return influenceRows > 0;
    }

    @Override
    public boolean updateTaskRoute(TaskRouteEntity entity) {
        int influenceRows = getDbSqlSession().getSqlSession().update(this.getClass().getName() + ".update", entity);
        return influenceRows > 0;
    }

    @Override
    public TaskRouteEntity findTaskRouteEntityById(String taskId) {
        TaskRouteEntity result = getDbSqlSession().getSqlSession()
                .selectOne(this.getClass().getName() + ".selectByTaskId", ParamMap.getInstance().add("taskId", taskId));
        return result;
    }
}
