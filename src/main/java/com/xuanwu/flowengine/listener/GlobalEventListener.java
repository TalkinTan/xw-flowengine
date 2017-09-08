package com.xuanwu.flowengine.listener;

import com.xuanwu.flowengine.cmd.GetFlowNodeDetailCmd;
import com.xuanwu.flowengine.cmd.SaveTaskRouteCmd;
import com.xuanwu.flowengine.entity.FlowNodeDetail;
import com.xuanwu.flowengine.entity.TaskRouteEntity;
import com.xuanwu.flowengine.extend.CustomMultiTenantProcessEngineConfiguration;
import com.xuanwu.flowengine.service.TaskRouteService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiActivityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityWithVariablesEventImpl;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 增加全局事件监听器
 * File created on 2017/6/1.
 *
 * @author jkun
 */
public class GlobalEventListener implements ActivitiEventListener {

    protected static ThreadLocal<TaskRouteEntity> incomingTaskInfoHolder = new ThreadLocal<>();

    private static final Logger logger = LoggerFactory.getLogger(GlobalEventListener.class);

    @Override
    public void onEvent(ActivitiEvent event) {
        ActivitiEventType eventType = event.getType();
        switch (eventType) {
            case ACTIVITY_STARTED:
                onActivityStart((ActivitiActivityEventImpl) event);
                break;
            case ACTIVITY_COMPLETED:
                onActivityComplete((ActivitiActivityEventImpl) event);
                break;
            case TASK_COMPLETED:
                onTaskComplete((ActivitiEntityWithVariablesEventImpl) event);
                break;
            case TASK_CREATED:
                onTaskCreate((ActivitiEntityEventImpl) event);
                break;
            default:
                break;
        }
    }

    private void onActivityStart(ActivitiActivityEventImpl event) {
    }

    private void onActivityComplete(ActivitiActivityEventImpl event) {
    }

    /**
     * 全局taskComplete事件监听处理器
     *
     * @param event
     */
    private void onTaskComplete(ActivitiEntityWithVariablesEventImpl event) {
        TaskEntity taskEntity = (TaskEntity) event.getEntity();
        Map variables = event.getVariables();
        String reason = MapUtils.getString(variables, "deleteReason", "completed");

        try {
            CustomMultiTenantProcessEngineConfiguration configuration = (CustomMultiTenantProcessEngineConfiguration) event.getEngineServices().getProcessEngineConfiguration();
            TaskRouteService service = configuration.getTaskRouteService();
            TaskRouteEntity routeEntity = service.findTaskRouteById(taskEntity.getId());
            /*if (null == routeEntity) {
                throw new ActivitiException("can not find the task route record , taskid is :" + taskEntity.getId());
            }*/
            // 暂不处理找不到routeEntity的异常
            if (routeEntity != null) {
                routeEntity.setEndTime(event.getEngineServices().getProcessEngineConfiguration().getClock().getCurrentTime());
                routeEntity.setJumpOutType(reason);
                // update taskroute record
                service.updateTaskRoute(routeEntity);
                // 设置线程变量
                incomingTaskInfoHolder.set(routeEntity);
            }
        } catch (Exception ex) {
            logger.error("自定义的TaskComplete事件处理器处理失败：" + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 全局taskCreate事件监听器处理
     *
     * @param event
     */
    private void onTaskCreate(ActivitiEntityEventImpl event) {
        TaskEntity task = (TaskEntity) event.getEntity();
        RepositoryService repositoryService = event.getEngineServices().getRepositoryService();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(event.getProcessDefinitionId());
        ActivityImpl taskActivity = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());

        TaskRouteEntity routeEntity = new TaskRouteEntity();
        routeEntity.setProcessInstanceId(task.getProcessInstanceId());
        routeEntity.setProcessExecutionId(task.getExecutionId());
        routeEntity.setProcessDefineId(task.getProcessDefinitionId());
        routeEntity.setTaskInstanceId(task.getId());
        routeEntity.setTaskKey(task.getTaskDefinitionKey());
        routeEntity.setTaskName(task.getName());
        routeEntity.setStartTime(task.getCreateTime());
        if (taskActivity.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
            routeEntity.setJointNode(true);
        }

        try {
            if (incomingTaskInfoHolder.get() != null) {
                TaskRouteEntity prevRouteInfo = incomingTaskInfoHolder.get();
                if (prevRouteInfo.isJointNode()) {
                    // 上一步骤是会审节点，本步骤需自动设置处理人
                    if (task.getAssignee() == null) {
                        FlowNodeDetail nodeDetail = event.getEngineServices().getManagementService().executeCommand(new GetFlowNodeDetailCmd(task.getTaskDefinitionKey(),
                                task.getProcessInstanceId(), task.getProcessDefinitionId(), null));
                        if (null != nodeDetail && null != nodeDetail.getNodeCandidates() && nodeDetail.getNodeCandidates().size() > 0) {
                            task.setAssignee(nodeDetail.getNodeCandidates().iterator().next().getMemberId().toString());
                        }
                    }
                }

                routeEntity.setJumpInType(prevRouteInfo.getJumpOutType());
                routeEntity.setPrevRouteId(prevRouteInfo.getRouteId());
            }
            // save TaskRouteEntity
            ManagementService managementService = event.getEngineServices().getManagementService();
            managementService.executeCommand(new SaveTaskRouteCmd(routeEntity));
        } catch (Exception ex) {
            logger.error("自定义的taskCreate事件处理器处理失败：" + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }
}
