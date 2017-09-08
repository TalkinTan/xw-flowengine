package com.xuanwu.flowengine.service;

import com.xuanwu.flowengine.entity.ProcessStepDetail;
import com.xuanwu.flowengine.entity.ProcessTraceInfo;
import com.xuanwu.flowengine.util.DateConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.xuanwu.flowengine.entity.ProcessChoice.CHOICENAME_VARIABLE_KEY;
import static com.xuanwu.flowengine.entity.ProcessChoice.CHOICE_VARIABLE_KEY;

/**
 * 流程追踪服务
 * Created by Administrator on 2017/2/14.
 *
 * @author jkun
 */
public class ProcessTraceService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessTraceService.class);

    private ProcessEngine processEngine;
    private TaskService taskService;
    private HistoryService historyService;
    private IdentityService identityService;

    private DateConverter dateConverter = new DateConverter();

    public ProcessTraceService(ProcessEngine processEngine) {
        this.processEngine = processEngine;
        this.taskService = processEngine.getTaskService();
        this.historyService = processEngine.getHistoryService();
        this.identityService = processEngine.getIdentityService();
    }

    /**
     * 根据流程实例Id获取流程追踪历史信息
     *
     * @param processInstanceId
     * @return
     */
    public List<ProcessTraceInfo> getProcessTraces(String processInstanceId, ProcessStepDetail stepDetail) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        List<HistoricActivityInstance> historicActivities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime()
                .asc().orderByHistoricActivityInstanceEndTime().asc().list();
        if (historicProcessInstance != null) {
            stepDetail.setHasEnd(historicProcessInstance.getEndTime() != null);
            stepDetail.setBusinessKey(historicProcessInstance.getBusinessKey());
            stepDetail.setProcessInstanceName(historicProcessInstance.getName());
        }

        if (null != historicActivities && historicActivities.size() > 0) {
            List<ProcessTraceInfo> traces = new ArrayList<ProcessTraceInfo>();
            ProcessTraceInfo singleTrace = null;

            for (HistoricActivityInstance activity : historicActivities) {
                singleTrace = new ProcessTraceInfo();
                singleTrace.setActId(activity.getActivityId());
                singleTrace.setActName(activity.getActivityName());
                singleTrace.setActType(activity.getActivityType());
                if (null != activity.getEndTime()) {
                    singleTrace.setHandleTime(dateConverter.convert(String.class, activity.getEndTime()).toString());
                }

                if (activity.getActivityType().equals("userTask")) {
                    List<Comment> comments = taskService.getTaskComments(activity.getTaskId());
                    if (null != comments && comments.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        comments.forEach(item -> {
                            sb.append(item.getFullMessage());
                        });

                        singleTrace.setComments(sb.toString());
                    }

                    HistoricVariableInstance variableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                            .taskId(activity.getTaskId()).variableName(CHOICE_VARIABLE_KEY).singleResult();
                    if (null != variableInstance && variableInstance.getValue() != null) {
                        singleTrace.setChoice((int) variableInstance.getValue());
                    }

                    variableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                            .taskId(activity.getTaskId()).variableName(CHOICENAME_VARIABLE_KEY).singleResult();

                    if (null != variableInstance && variableInstance.getValue() != null) {
                        singleTrace.setChoiceName(variableInstance.getValue().toString());
                    }

                    String assignee = activity.getAssignee();
                    if (null != assignee) {
                        singleTrace.setAssigneeCode(assignee);
                        User user = identityService.createUserQuery().userId(assignee).singleResult();
                        if (null != user) {
                            singleTrace.setAssignee(user.getFirstName());
                        }
                    }
                } else if (!activity.getActivityType().equals("endEvent") && !activity.getActivityType().equals("startEvent")) {
                    continue;
                }

                traces.add(singleTrace);
            }

            return traces;
        }

        return null;
    }

}
