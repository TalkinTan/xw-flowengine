package com.xuanwu.flowengine.mapper.execution;

import com.xuanwu.flowengine.entity.FlowCategoryEntity;
import com.xuanwu.flowengine.mapper.FlowCategoryMapper;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 流程分类查询执行器 (查询所有和通过编码查询)
 * File created on 2017/6/19.
 *
 * @author jkun
 */
public class FlowCategoryQueryExecution extends AbstractCustomSqlExecution<FlowCategoryMapper, List<FlowCategoryEntity>> {

    private static final Logger logger = LoggerFactory.getLogger(FlowCategoryQueryExecution.class);

    private Long flowCategoryCode;

    public FlowCategoryQueryExecution() {
        super(FlowCategoryMapper.class);
    }

    public FlowCategoryQueryExecution FlowCategoryCode(Long flowCategoryCode) {
        this.flowCategoryCode = flowCategoryCode;
        return this;
    }

    @Override
    public List<FlowCategoryEntity> execute(FlowCategoryMapper flowCategoryMapper) {
        if (null == flowCategoryCode) {
            // query flowcategory list
            logger.debug("start query flowcategory list");
            return flowCategoryMapper.selectAll();
        } else {
            // query single flowcategory record by flowcategorycode
            logger.debug("start query single flowcategory record by flowcategorycode");
            FlowCategoryEntity queryResult = flowCategoryMapper.selectById(flowCategoryCode);
            return Arrays.asList(queryResult);
        }
    }
}
