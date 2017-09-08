package com.xuanwu.flowengine.mapper;

import com.xuanwu.flowengine.entity.TaskEntityWrapper;
import com.xuanwu.flowengine.entity.dto.FlowMessageParam;

import java.util.List;
import java.util.Map;

/**
 * 自定义待办已办Mapper接口,对应CustomTaskList-mapper.xml定义的查询
 * File created on 2017/6/14.
 *
 * @author jkun
 */
public interface CustomTaskListMapper {

    /**
     * 待办数量
     *
     * @param param
     * @return
     */
    long getMyToDoCount(Map param);

    /**
     * 待办分页列表
     *
     * @param param
     * @return
     */
    List<TaskEntityWrapper> getMyToDoPageList(Map param);

    /**
     * 已办数量
     *
     * @param param
     * @return
     */
    long getMyHaveDoneCount(Map param);

    /**
     * 已办分页列表
     *
     * @param param
     * @return
     */
    List<TaskEntityWrapper> getMyHaveDonePageList(Map param);

    /**
     * 获取流程当前的任务信息
     *
     * @param param
     * @return
     */
    FlowMessageParam getCurrentTaskInfo(Map param);
}
