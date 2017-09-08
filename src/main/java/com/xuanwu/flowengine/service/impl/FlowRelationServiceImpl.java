package com.xuanwu.flowengine.service.impl;

import com.xuanwu.flowengine.cmd.DeleteFlowRelationCmd;
import com.xuanwu.flowengine.cmd.FlowRelationQueryImpl;
import com.xuanwu.flowengine.cmd.SaveFlowDefineRelationCmd;
import com.xuanwu.flowengine.entity.FlowCategoryRelationEntity;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.dto.FlowCategoryRelationListDto;
import com.xuanwu.flowengine.service.FlowRelationService;
import org.activiti.engine.impl.ServiceImpl;

/**
 * 流程关联关系服务实现类
 * Created by jkun on 2017/3/17.
 *
 * @author jkun
 */
public class FlowRelationServiceImpl extends ServiceImpl implements FlowRelationService {
    @Override
    public Page<FlowCategoryRelationListDto> getFlowRelationListPage(long diretoryCode, String flowModelName, int pageIndex, int pageSize) {
        FlowRelationQueryImpl query = new FlowRelationQueryImpl(commandExecutor);
        Page<FlowCategoryRelationListDto> result = query.directoryCode(diretoryCode).flowModelName(flowModelName)
                .listPage(pageIndex, pageSize);

        return result;
    }

    @Override
    public boolean saveFlowRelation(FlowCategoryRelationEntity entity) {
        return commandExecutor.execute(new SaveFlowDefineRelationCmd(entity));
    }

    @Override
    public boolean deleteFlowRelationByCode(Long flowRelationCode) {
        return commandExecutor.execute(new DeleteFlowRelationCmd(flowRelationCode));
    }
}
