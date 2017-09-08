package com.xuanwu.flowengine.cmd;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.InterpretableExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 节点自由跳转命令
 * Created by jkun on 2017/2/20.
 *
 * @author jkun
 */
public class AutoJumpTaskCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AutoJumpTaskCmd.class);
    protected String executionId;
    protected ActivityImpl currentActivity;
    protected Map<String, Object> parms;
    protected ActivityImpl desActivity;
    protected String reason;

    public AutoJumpTaskCmd(String executionId, ActivityImpl currentActivity, ActivityImpl desActivity, Map<String, Object> parms, String reason) {
        this.executionId = executionId;
        this.currentActivity = currentActivity;
        this.desActivity = desActivity;
        this.parms = parms;
        this.reason = reason;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(executionId);
        executionEntity.setActivity(currentActivity);
        executionEntity.setEventSource(currentActivity);
        executionEntity.setVariables(parms);

        List<TaskEntity> taskEntities = commandContext.getTaskEntityManager().findTasksByExecutionId(executionId);
        parms.put("deleteReason", reason);

        for (TaskEntity entity : taskEntities) {
            entity.fireEvent("complete");

            // Dispatch Event
            if (Context.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
                Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
                        ActivitiEventBuilder.createEntityWithVariablesEvent(ActivitiEventType.TASK_COMPLETED, entity, parms, false)
                );
            }
        }

        executionEntity.destroyScope(reason);
        if (desActivity.isScope()) {
            // 退回到会审节点需做特殊处理, execution分化
            InterpretableExecution propagatingExecution = executionEntity.createExecution();
            propagatingExecution.setActivity(desActivity);
            propagatingExecution.setTransition(null);
            executionEntity.setTransition(null);
            executionEntity.setActivity(null);
            executionEntity.setActive(false);
            logger.debug("create scope: parent {} continues as execution {}", executionEntity, propagatingExecution);
            propagatingExecution.initialize();
            propagatingExecution.executeActivity(desActivity);
        } else {
            executionEntity.setDeleteReason(reason);
            executionEntity.executeActivity(desActivity);
        }

        return null;
    }
}
