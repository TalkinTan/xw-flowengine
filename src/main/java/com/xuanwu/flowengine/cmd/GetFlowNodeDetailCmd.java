package com.xuanwu.flowengine.cmd;

import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import com.xuanwu.flowengine.entity.FlowNodeDetail;
import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.extend.CustomUserEntityManager;
import com.xuanwu.flowengine.strategy.*;
import com.xuanwu.flowengine.util.WorkflowUtils;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 获取节点详情命令 （节点基本信息以及人员策略定义的候选人员范围）
 * File created on 2017/5/26.
 *
 * @author jkun
 */
public class GetFlowNodeDetailCmd implements Command<FlowNodeDetail> {

    private static final Logger logger = LoggerFactory.getLogger(GetFlowNodeDetailCmd.class);
    private String nodeKey;
    private String processInstanceId;
    private String processDefineId;
    private String prevStepHandler;

    public GetFlowNodeDetailCmd(String nodeKey, String processInstanceId, String processDefineId, String prevStepHanlder) {
        this.nodeKey = nodeKey;
        this.processDefineId = processDefineId;
        this.processInstanceId = processInstanceId;
        this.prevStepHandler = prevStepHanlder;
    }

    @Override
    public FlowNodeDetail execute(CommandContext commandContext) {
        if (StringUtils.isEmpty(processDefineId)) {
            throw new ActivitiIllegalArgumentException("parameter processdefineid can not be null!");
        }
        ProcessDefinitionEntity definitionEntity = commandContext.getProcessEngineConfiguration().getDeploymentManager()
                .findDeployedProcessDefinitionById(processDefineId);

        if (StringUtils.isEmpty(nodeKey)) {
            throw new ActivitiIllegalArgumentException("parameter taskkey can not be null!");
        }
        ActivityImpl activity = definitionEntity.findActivity(nodeKey);
        if (!activity.getProperty("type").equals("userTask")) {
            throw new ActivitiException("the type of node is not usertask!");
        }

        FlowNodeDetail flowNodeDetail = new FlowNodeDetail();
        flowNodeDetail.setNodeKey(activity.getId());
        flowNodeDetail.setNodeName(activity.getProperty("name").toString());
        flowNodeDetail.setNodeType(activity.getProperty("type").toString());
        flowNodeDetail.setJointNode(activity.getActivityBehavior() instanceof ParallelMultiInstanceBehavior);
        flowNodeDetail.setMode("manual");
        flowNodeDetail.setNodeCandidates(getTaskCandidateUserRange(activity, commandContext));

        return flowNodeDetail;
    }

    private Collection<MemberBasicInfoDto> getTaskCandidateUserRange(ActivityImpl activity, CommandContext context) {
        TaskDefinition taskDefinition = (TaskDefinition) activity.getProperty("taskDefinition");
        Expression assigneeExpression = taskDefinition.getAssigneeExpression();
        Set<Expression> candidateUserExpressions = taskDefinition.getCandidateUserIdExpressions();       // 候选人
        CustomUserEntityManager manager = (CustomUserEntityManager) context.getUserIdentityManager();
        ExecutionEntity execution = context.getExecutionEntityManager().findExecutionById(processInstanceId);  // 执行实例对象
        Collection<String> candidateUsers = null;

        // 优先判断固定处理人策略
        if (null != assigneeExpression && !(activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior)) {
            // 非多实例任务获取配置处理人信息
            Object assigneeExpressionValue = assigneeExpression.getValue(execution);
            String assigneeValue = null;
            if (assigneeExpressionValue != null) {
                assigneeValue = assigneeExpressionValue.toString();
                if (StringUtils.isNotEmpty(assigneeValue)) {
                    //指定处理人为最高优先级
                    MemberBasicInfoDto member = manager.findMemberInfoByMemberId(assigneeValue);
                    return Arrays.asList(member);
                }
            }
        }

        // 候选人策略
        if (null != candidateUserExpressions && !candidateUserExpressions.isEmpty()) {
            candidateUsers = new ArrayList<>();
            for (Expression userIdExpr : candidateUserExpressions) {
                Object value = userIdExpr.getValue(execution);
                if (value instanceof String) {
                    candidateUsers.addAll(WorkflowUtils.extractString(value.toString(), ","));
                } else if (value instanceof Collection) {
                    candidateUsers.addAll((Collection<String>) value);
                } else {
                    throw new ActivitiIllegalArgumentException("Expression did not resolve to a string or collection of strings");
                }
            }

            if (null != candidateUsers && candidateUsers.size() > 0) {
                List<MemberBasicInfoDto> userlist = manager.batchQueryMemberInfo(candidateUsers);
                return new HashSet<>(userlist);
            }
        }

        // 自定义的混合策略
        Object userStrategy = activity.getProperty(ExtendFlowElementConstant.EXTEND_USER_STRATEGY_NAMESPACE);
        if (null != userStrategy && userStrategy instanceof HashMap) {
            // 获取配置的策略信息
            Map map = (HashMap) userStrategy;
            String type = MapUtils.getString(map, ExtendFlowElementConstant.EXTEND_USERSTRATEGY_TYPE);
            String orgValues = MapUtils.getString(map, ExtendFlowElementConstant.EXTEND_USERSTRATEGY_ORGVALUE);
            String postValues = MapUtils.getString(map, ExtendFlowElementConstant.EXTEND_USERSTRATEGY_POSTVALUE);

            if (null != type) {
                BaseUserStrategy strategy = null;
                if (type.equals(ExtendFlowElementConstant.fixedOrgTypeName)) {
                    // 固定组织策略
                    strategy = new FixedOrgStrategy(manager, orgValues);
                } else if (type.equals(ExtendFlowElementConstant.fixedPostTypeName)) {
                    // 固定岗位策略
                    strategy = new FixedPostStrategy(manager, postValues);

                } else if (ArrayUtils.contains(ExtendFlowElementConstant.applyRelateType, type)) {
                    // 发起人运行时相关特殊策略
                    HistoricProcessInstance historyInstance = context.getHistoricProcessInstanceEntityManager()
                            .findHistoricProcessInstance(execution.getProcessInstanceId());
                    String applyUserCode = historyInstance.getStartUserId();    // 获取发起人
                    strategy = new ApplyRelateStrategy(manager, ExtendFlowElementConstant.userStrategyTypeMapper.get(type), applyUserCode);
                } else if (ArrayUtils.contains(ExtendFlowElementConstant.prevStepHandlerRelateType, type)) {
                    // 上一步骤处理人运行时相关特殊策略
                    if (null != prevStepHandler) {
                        strategy = new PrevHandlerRelateStrategy(manager, ExtendFlowElementConstant.userStrategyTypeMapper.get(type), prevStepHandler);
                    }
                }

                if (null != strategy) {
                    if (StringUtils.isNotEmpty(postValues)) {
                        strategy.setPostValues(postValues);
                    }

                    return strategy.execute();
                }
            }
        }

        return null;
    }
}
