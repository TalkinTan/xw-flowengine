package com.xuanwu.flowengine.service.impl;

import com.xuanwu.flowengine.entity.FlowCategoryEntity;
import com.xuanwu.flowengine.entity.OperateType;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.dto.FlowCategoryDetailDto;
import com.xuanwu.flowengine.mapper.execution.FlowCategoryManageExecution;
import com.xuanwu.flowengine.mapper.execution.FlowCategoryQueryDetailExecution;
import com.xuanwu.flowengine.mapper.execution.FlowCategoryQueryExecution;
import com.xuanwu.flowengine.mapper.execution.FlowCategoryQueryProcessExecution;
import com.xuanwu.flowengine.service.FlowCategoryService;
import org.activiti.engine.impl.ServiceImpl;
import org.activiti.engine.impl.cmd.ExecuteCustomSqlCmd;

import java.util.List;
import java.util.Map;

/**
 * 流程种类服务实现类
 * Created by jkun on 2017/3/15.
 *
 * @author jkun
 */
public class FlowCategoryServiceImpl extends ServiceImpl implements FlowCategoryService {
    @Override
    public boolean insertFlowCategory(FlowCategoryEntity entity) {
        FlowCategoryManageExecution execution = new FlowCategoryManageExecution();
        execution.Entity(entity).OperateType(OperateType.INSERT);
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(execution.getMapperClass(), execution));
    }

    @Override
    public boolean updateFlowCategory(FlowCategoryEntity entity) {
        FlowCategoryManageExecution execution = new FlowCategoryManageExecution();
        execution.Entity(entity).OperateType(OperateType.UPDATE);
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(execution.getMapperClass(), execution));
    }

    @Override
    public boolean deleteFlowCategory(Long flowCategoryCode) {
        FlowCategoryManageExecution execution = new FlowCategoryManageExecution();
        execution.FlowCategoryCode(flowCategoryCode).OperateType(OperateType.DELETE);
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(execution.getMapperClass(), execution));
    }

    @Override
    public List<FlowCategoryEntity> selectAll() {
        FlowCategoryQueryExecution queryExecution = new FlowCategoryQueryExecution();
        List<FlowCategoryEntity> queryResult = commandExecutor.execute(new ExecuteCustomSqlCmd<>(queryExecution.getMapperClass(), queryExecution));
        return queryResult;
    }

    @Override
    public FlowCategoryEntity getFlowCategoryByKeys(Long flowCategoryCode) {
        FlowCategoryQueryExecution queryExecution = new FlowCategoryQueryExecution();
        queryExecution.FlowCategoryCode(flowCategoryCode);
        List<FlowCategoryEntity> queryResult = commandExecutor.execute(new ExecuteCustomSqlCmd<>(queryExecution.getMapperClass(), queryExecution));

        if (null != queryResult && queryResult.size() == 1) {
            return queryResult.get(0);
        }
        return null;
    }

    /**
     * old接口不带权限，供630测试，新的接口为getFlowCategoryDetailWithPermission(Long)
     *
     * @return
     */
    @Override
    public List<FlowCategoryDetailDto> getFlowCategoryDetail() {
        FlowCategoryQueryDetailExecution queryDetailExecution = new FlowCategoryQueryDetailExecution(null);
        List<FlowCategoryDetailDto> queryResult = commandExecutor.execute(new ExecuteCustomSqlCmd<>(queryDetailExecution.getMapperClass(), queryDetailExecution));
        return queryResult;
    }

    @Override
    public List<FlowCategoryDetailDto> getFlowCategoryDetailWithPermission(Long positionId) {
        FlowCategoryQueryDetailExecution queryDetailExecution = new FlowCategoryQueryDetailExecution(positionId);
        List<FlowCategoryDetailDto> queryResult = commandExecutor.execute(new ExecuteCustomSqlCmd<>(queryDetailExecution.getMapperClass(), queryDetailExecution));
        return queryResult;
    }

    @Override
    public boolean checkPermissionByProcessKey(Long positionId, String processKey) {
        FlowCategoryManageExecution execution = new FlowCategoryManageExecution();
        execution.positionId(positionId).processKey(processKey).OperateType(OperateType.CHECKPERMISSION);
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(execution.getMapperClass(), execution));
    }

    @Override
    public Page<Map> getProcessPageListByCategoryCode(Long flowCategoryCode, int pageIndex, int pageSize) {
        FlowCategoryQueryProcessExecution execution = new FlowCategoryQueryProcessExecution();
        execution.flowCategoryCode(flowCategoryCode).pageIndex(pageIndex).pageSize(pageSize);
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(execution.getMapperClass(), execution));
    }
}
