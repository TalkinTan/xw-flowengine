package com.xuanwu.flowengine.service;

import com.xuanwu.flowengine.entity.TaskRouteEntity;

/**
 * 自定义任务流转记录服务接口层
 * File created on 2017/6/13.
 *
 * @author jkun
 */
public interface TaskRouteService {

    /**
     * 插入
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
    TaskRouteEntity findTaskRouteById(String taskId);
}
