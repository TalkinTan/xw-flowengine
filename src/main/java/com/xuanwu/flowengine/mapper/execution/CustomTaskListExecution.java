package com.xuanwu.flowengine.mapper.execution;

import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.ProcessStatus;
import com.xuanwu.flowengine.entity.TaskEntityWrapper;
import com.xuanwu.flowengine.mapper.CustomTaskListMapper;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 自定义待办已办的查询器
 * File created on 2017/6/14.
 *
 * @author jkun
 */
public class CustomTaskListExecution extends AbstractCustomSqlExecution<CustomTaskListMapper, Page<TaskEntityWrapper>> {

    private static final Logger logger = LoggerFactory.getLogger(CustomTaskListExecution.class);

    private String memberId;
    private int pageIndex;
    private int pageSize;
    private int queryType;
    private Map conditionMap;

    public CustomTaskListExecution(String memberId, int pageIndex, int pageSize, int queryType, Map conditionMap) {
        super(CustomTaskListMapper.class);
        this.memberId = memberId;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.queryType = queryType;
        this.conditionMap = conditionMap;
    }

    @Override
    public Page<TaskEntityWrapper> execute(CustomTaskListMapper customTaskListMapper) {
        if (null == memberId) {
            logger.error("查询待办的memberId不能为空!");
            throw new ActivitiException("查询待办的memberId不能为空!");
        }

        if (queryType == 0) {
            return getMyToDoList(customTaskListMapper);
        } else {
            return getMyHaveDone(customTaskListMapper);
        }
    }

    protected Page<TaskEntityWrapper> getMyToDoList(CustomTaskListMapper customTaskListMapper) {
        conditionMap.put("memberId", memberId);
        long recordCount = customTaskListMapper.getMyToDoCount(conditionMap);
        if (recordCount == 0) {
            return new Page<>(pageSize, 0, null);
        }

        conditionMap.put("limit", pageSize);
        conditionMap.put("offset", (pageIndex - 1) * pageSize);
        List<TaskEntityWrapper> queryResult = customTaskListMapper.getMyToDoPageList(conditionMap);
        if (null != queryResult && queryResult.size() > 0) {
            for (TaskEntityWrapper wrapper : queryResult) {
                // 处理状态字段
                if (wrapper.isFallBack()) {
                    wrapper.setStatus(ProcessStatus.FALLBACK);
                } else {
                    if (wrapper.getTaskKey().equals(ExtendFlowElementConstant.EXTEND_FIRSTSTEP_ID)) {
                        wrapper.setStatus(ProcessStatus.DRAFT);
                    } else {
                        wrapper.setStatus(ProcessStatus.HANDLING);
                    }
                }
            }
        }

        return new Page(pageSize, recordCount, queryResult);
    }

    protected Page<TaskEntityWrapper> getMyHaveDone(CustomTaskListMapper customTaskListMapper) {
        conditionMap.put("memberId", memberId);
        long recordCount = customTaskListMapper.getMyHaveDoneCount(conditionMap);
        if (recordCount == 0) {
            return new Page<>(pageSize, 0, null);
        }

        conditionMap.put("limit", pageSize);
        conditionMap.put("offset", (pageIndex - 1) * pageSize);
        List<TaskEntityWrapper> queryResult = customTaskListMapper.getMyHaveDonePageList(conditionMap);
        if (null != queryResult && queryResult.size() > 0) {
            for (TaskEntityWrapper wrapper : queryResult) {
                if (wrapper.isHasEnd()) {
                    if (StringUtils.isNotEmpty(wrapper.getDeleteReason()) && wrapper.getDeleteReason().equals("abandon")) {
                        wrapper.setStatus(ProcessStatus.ABANDON);
                    } else {
                        wrapper.setStatus(ProcessStatus.HAS_END);
                    }
                } else {
                    wrapper.setStatus(ProcessStatus.HANDLING);
                }
            }
        }

        return new Page(pageSize, recordCount, queryResult);
    }
}
