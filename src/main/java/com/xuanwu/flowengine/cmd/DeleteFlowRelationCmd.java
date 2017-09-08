package com.xuanwu.flowengine.cmd;

import com.xuanwu.flowengine.dao.FlowRelationDao;
import com.xuanwu.flowengine.dao.impl.FlowRelationDaoImpl;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 删除流程关联关系
 * File created on 2017/5/16.
 *
 * @author jkun
 */
public class DeleteFlowRelationCmd implements Command<Boolean>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DeleteFlowRelationCmd.class);
    private Long relationCode;
    private FlowRelationDao dao = new FlowRelationDaoImpl();


    public DeleteFlowRelationCmd(Long relationCode) {
        this.relationCode = relationCode;
    }

    @Override
    public Boolean execute(CommandContext commandContext) {
        if (null == relationCode) {
            throw new ActivitiIllegalArgumentException("relationCode不能为空!");
        }

        dao.deleteFlowRelation(relationCode);
        return true;
    }
}
