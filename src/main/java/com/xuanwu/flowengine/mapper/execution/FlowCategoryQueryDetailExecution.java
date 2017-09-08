package com.xuanwu.flowengine.mapper.execution;

import com.xuanwu.flowengine.entity.dto.FlowCategoryDetailDto;
import com.xuanwu.flowengine.mapper.FlowCategoryMapper;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 供手机端使用的流程分类列表详情查询, 附带权限控制
 * File created on 2017/6/19.
 *
 * @author jkun
 */
public class FlowCategoryQueryDetailExecution extends AbstractCustomSqlExecution<FlowCategoryMapper, List<FlowCategoryDetailDto>> {

    private static final Logger logger = LoggerFactory.getLogger(FlowCategoryQueryDetailExecution.class);

    private Long positionId;

    public FlowCategoryQueryDetailExecution(Long positionId) {
        super(FlowCategoryMapper.class);
        this.positionId = positionId;
    }

    @Override
    public List<FlowCategoryDetailDto> execute(FlowCategoryMapper flowCategoryMapper) {
        logger.debug("start query flowcategory detail");

        // 配合630测试，暂时使用不带权限的接口
        return flowCategoryMapper.selectDetailWithoutPrivilege();

        /*if (null == positionId) {
            logger.error("获取流程分类列表详情时岗位编码不能为空!");
            throw new ActivitiException("获取流程分类列表详情时岗位编码不能为空!");
        }

        return flowCategoryMapper.selectCategoryDetailWithPrivilege(positionId);*/
    }
}
