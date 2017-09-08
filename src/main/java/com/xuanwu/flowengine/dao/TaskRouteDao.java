package com.xuanwu.flowengine.dao;

import com.xuanwu.flowengine.entity.TaskRouteEntity;

/**
 * 自定义任务流转记录数据访问接口层
 * File created on 2017/6/13.
 *
 * @author jkun
 */
public interface TaskRouteDao {

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insertTaskRoute(TaskRouteEntity entity);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    boolean updateTaskRoute(TaskRouteEntity entity);

    /**
     * 根据任务实例Id查询taskRoute信息
     *
     * @param taskId
     * @return
     */
    TaskRouteEntity findTaskRouteEntityById(String taskId);
}
