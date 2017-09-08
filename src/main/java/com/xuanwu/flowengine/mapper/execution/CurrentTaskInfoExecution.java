package com.xuanwu.flowengine.mapper.execution;

import com.xuanwu.flowengine.entity.dto.FlowMessageParam;
import com.xuanwu.flowengine.mapper.CustomTaskListMapper;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;

import java.util.HashMap;
import java.util.Map;

/**
 * File created on 2017/8/7.
 *
 * @author jkun
 */
public class CurrentTaskInfoExecution extends AbstractCustomSqlExecution<CustomTaskListMapper, FlowMessageParam> {

    private String processInstanceId;

    public CurrentTaskInfoExecution(String processInstanceId) {
        super(CustomTaskListMapper.class);
        this.processInstanceId = processInstanceId;
    }

    @Override
    public FlowMessageParam execute(CustomTaskListMapper customTaskListMapper) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("processinstanceid", processInstanceId);

        FlowMessageParam result = customTaskListMapper.getCurrentTaskInfo(map);
        return result;
    }
}
