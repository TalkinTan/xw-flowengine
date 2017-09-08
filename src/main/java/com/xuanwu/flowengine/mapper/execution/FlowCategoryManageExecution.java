package com.xuanwu.flowengine.mapper.execution;

import com.xuanwu.flowengine.context.ProcessContext;
import com.xuanwu.flowengine.entity.FlowCategoryEntity;
import com.xuanwu.flowengine.entity.OperateType;
import com.xuanwu.flowengine.mapper.FlowCategoryMapper;
import com.xuanwu.flowengine.util.ParamMap;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流程分类管理接口执行器
 * File created on 2017/6/17.
 *
 * @author jkun
 */
public class FlowCategoryManageExecution extends AbstractCustomSqlExecution<FlowCategoryMapper, Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(FlowCategoryManageExecution.class);

    /**
     * 操作类型 （新增、更新、删除）
     */
    private OperateType operateType;

    /**
     * 新增和更新时操作的FlowCategory实体
     */
    private FlowCategoryEntity entity;

    /**
     * 删除操作时的流程分类编码
     */
    private Long flowCategoryCode;

    /**
     * 核查流程发起权限时的岗位编码
     */
    private Long positionId;

    /**
     * 核查流程发起权限时的流程定义Key
     */
    private String processKey;

    public FlowCategoryManageExecution OperateType(OperateType operateType) {
        this.operateType = operateType;
        return this;
    }

    public FlowCategoryManageExecution Entity(FlowCategoryEntity entity) {
        this.entity = entity;
        return this;
    }

    public FlowCategoryManageExecution FlowCategoryCode(Long flowCategoryCode) {
        this.flowCategoryCode = flowCategoryCode;
        return this;
    }

    public FlowCategoryManageExecution positionId(Long positionId) {
        this.positionId = positionId;
        return this;
    }

    public FlowCategoryManageExecution processKey(String processKey) {
        this.processKey = processKey;
        return this;
    }

    public FlowCategoryManageExecution() {
        super(FlowCategoryMapper.class);
    }

    @Override
    public Boolean execute(FlowCategoryMapper flowCategoryMapper) {
        if (null == operateType) {
            logger.error("操作类型不能为空!");
            throw new ActivitiException("操作类型不能为空!");
        }

        boolean operateResult = false;
        switch (operateType) {
            case INSERT:
                operateResult = insertOpr(flowCategoryMapper);
                break;
            case UPDATE:
                operateResult = updateOpr(flowCategoryMapper);
                break;
            case DELETE:
                operateResult = deleteOpr(flowCategoryMapper);
                break;
            case CHECKPERMISSION:
                operateResult = checkPermission(flowCategoryMapper);
                break;
            default:
                break;
        }

        return operateResult;
    }

    protected boolean insertOpr(FlowCategoryMapper flowCategoryMapper) {
        logger.debug("start insert the new flow category");

        IdGenerator generator = ProcessContext.getIdGenerator();
        entity.setFlowCategoryCode(Long.valueOf(generator.getNextId()));
        entity.setStatus(1);
        // 检查重名
        if (flowCategoryMapper.checkIfExists(ParamMap.getInstance().add("name", entity.getFlowCategoryName()).add("code", entity.getFlowCategoryCode())) > 0) {
            logger.error("已存在相同的流程分类名称!");
            throw new ActivitiException("已存在相同的流程分类名称!");
        }

        return flowCategoryMapper.insert(entity);
    }

    protected boolean updateOpr(FlowCategoryMapper flowCategoryMapper) {
        logger.debug("start update the flow category");

        // 检查重名
        if (flowCategoryMapper.checkIfExists(ParamMap.getInstance().add("name", entity.getFlowCategoryName()).add("code", entity.getFlowCategoryCode())) > 0) {
            logger.error("已存在相同的流程分类名称!");
            throw new ActivitiException("已存在相同的流程分类名称!");
        }

        return flowCategoryMapper.update(entity);
    }

    protected boolean deleteOpr(FlowCategoryMapper flowCategoryMapper) {
        logger.debug("start delete the flow category, just logic delete");

        if (null == flowCategoryCode) {
            logger.error("进行删除操作时，流程分类编码不能为空!");
            throw new ActivitiException("进行删除操作时，流程分类编码不能为空!");
        }

        return flowCategoryMapper.delete(flowCategoryCode);
    }

    protected boolean checkPermission(FlowCategoryMapper flowCategoryMapper) {
        logger.debug("start check the process permission");

        if (null == positionId || processKey == null) {
            logger.error("核查流程的发起权限时，岗位编码和流程定义Key参数不能为空");
            throw new ActivitiException("核查流程的发起权限时，岗位编码和流程定义Key参数不能为空");
        }

        ParamMap map = ParamMap.getInstance().add("positionId", positionId).add("processKey", processKey);
        return flowCategoryMapper.checkPrivilegeByProcessKey(map) > 0;
    }
}
