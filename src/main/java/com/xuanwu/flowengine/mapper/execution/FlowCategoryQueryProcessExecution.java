package com.xuanwu.flowengine.mapper.execution;

import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.mapper.FlowCategoryMapper;
import com.xuanwu.flowengine.util.ParamMap;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 获取流程分类下挂接的流程列表执行器
 * File created on 2017/6/20.
 *
 * @author jkun
 */
public class FlowCategoryQueryProcessExecution extends AbstractCustomSqlExecution<FlowCategoryMapper, Page<Map>> {

    private static final Logger logger = LoggerFactory.getLogger(FlowCategoryQueryProcessExecution.class);

    private Long flowCategoryCode;

    private int pageSize = Integer.MAX_VALUE;

    private int pageIndex = 1;

    public FlowCategoryQueryProcessExecution flowCategoryCode(Long flowCategoryCode) {
        this.flowCategoryCode = flowCategoryCode;
        return this;
    }

    public FlowCategoryQueryProcessExecution pageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public FlowCategoryQueryProcessExecution pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public FlowCategoryQueryProcessExecution() {
        super(FlowCategoryMapper.class);
    }

    @Override
    public Page<Map> execute(FlowCategoryMapper flowCategoryMapper) {
        logger.debug("start get processlist by flowcategory code");

        if (null == flowCategoryCode) {
            logger.error("获取流程分类下挂载的流程时分类编码不能为空!");
            throw new ActivitiException("获取流程分类下挂载的流程时分类编码不能为空!");
        }

        long recordCount = flowCategoryMapper.getProcessCountByCategoryCode(flowCategoryCode);
        if (recordCount == 0) {
            return new Page<>(pageSize, 0, null);
        }

        ParamMap map = ParamMap.getInstance().add("flowCategoryCode", flowCategoryCode).add("limit", pageSize).add("offset", (pageIndex - 1) * pageSize);
        List<Map> queryResult = flowCategoryMapper.getProcessListByCategoryCode(map);
        return new Page<>(pageSize, recordCount, queryResult);
    }
}
