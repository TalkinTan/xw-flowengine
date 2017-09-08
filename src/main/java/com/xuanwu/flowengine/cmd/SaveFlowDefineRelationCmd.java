package com.xuanwu.flowengine.cmd;

import com.xuanwu.flowengine.dao.FlowRelationDao;
import com.xuanwu.flowengine.dao.impl.FlowRelationDaoImpl;
import com.xuanwu.flowengine.entity.FlowCategoryRelationEntity;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程、目录、分类关联关系保存命令
 * Created by jkun on 2017/3/17.
 *
 * @author jkun
 */
public class SaveFlowDefineRelationCmd implements Command<Boolean>, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SaveFlowDefineRelationCmd.class);
    private static final long serialVersionUID = 1L;
    protected FlowCategoryRelationEntity flowCategoryRelationEntity;

    protected FlowRelationDao dao = new FlowRelationDaoImpl();

    public SaveFlowDefineRelationCmd(FlowCategoryRelationEntity flowCategoryRelationEntity) {
        this.flowCategoryRelationEntity = flowCategoryRelationEntity;
    }

    @Override
    public Boolean execute(CommandContext commandContext) {
        if (null == flowCategoryRelationEntity) {
            throw new ActivitiIllegalArgumentException("flowcategoryrelationentity is null!");
        }

        try {
            Date now = commandContext.getProcessEngineConfiguration().getClock().getCurrentTime();
            flowCategoryRelationEntity.setUpdateTime(now);
            if (null == flowCategoryRelationEntity.getFlowRelationCode()) {
                // insert
                String id = commandContext.getProcessEngineConfiguration().getIdGenerator().getNextId();
                flowCategoryRelationEntity.setFlowRelationCode(Long.valueOf(id));
                flowCategoryRelationEntity.setCreateTime(now);
                flowCategoryRelationEntity.setStatus(1);
                dao.insertFlowRelation(flowCategoryRelationEntity);
            } else {
                // update
                dao.updateFlowRelation(flowCategoryRelationEntity);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }

        return true;
    }
}


