package com.xuanwu.flowengine.cmd;

import com.xuanwu.flowengine.dao.FlowRelationDao;
import com.xuanwu.flowengine.dao.impl.FlowRelationDaoImpl;
import com.xuanwu.flowengine.entity.dto.FlowCategoryRelationListDto;
import com.xuanwu.flowengine.entity.Page;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;

import java.util.List;

/**
 * 流程、目录、种类关联查询实现类
 * Created by jkun on 2017/3/17.
 *
 * @author jkun
 */
public class FlowRelationQueryImpl extends AbstractQuery<FlowCategoryRelationListDto> {

    protected Long diretoryCode;
    protected String flowModelName;

    protected FlowRelationDao dao = new FlowRelationDaoImpl();

    public FlowRelationQueryImpl directoryCode(Long diretoryCode) {
        this.diretoryCode = diretoryCode;
        return this;
    }

    public FlowRelationQueryImpl flowModelName(String flowModelName) {
        this.flowModelName = flowModelName;
        return this;
    }


    public FlowRelationQueryImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public List<FlowCategoryRelationListDto> executeList(CommandContext commandContext) {
        return null;
    }

    @Override
    public Page<FlowCategoryRelationListDto> executePage(CommandContext commandContext) {
        Page<FlowCategoryRelationListDto> result = dao.getFlowListPage(diretoryCode, flowModelName, pageIndex, pageSize);
        return result;
    }
}
