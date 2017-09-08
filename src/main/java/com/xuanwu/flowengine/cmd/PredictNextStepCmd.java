package com.xuanwu.flowengine.cmd;

import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import com.xuanwu.flowengine.entity.FlowNodeDetail;
import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.extend.CustomUserEntityManager;
import com.xuanwu.flowengine.strategy.*;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.bpmn.behavior.*;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * 预提交获取下一步命令
 * <p>
 * Created by jkun on 2017/3/1.
 *
 * @author jkun
 */
public class PredictNextStepCmd implements Command<FlowNodeDetail>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(PredictNextStepCmd.class);
    private String processDefineId;
    private String taskId;
    private Map<String, Object> variables;
    private String currentTaskAssignee;   // 当前步骤的处理人Id

    public PredictNextStepCmd(String processDefineId, String taskId, Map<String, Object> variables) {
        this.processDefineId = processDefineId;
        this.taskId = taskId;
        this.variables = variables;
    }

    @Override
    public FlowNodeDetail execute(CommandContext commandContext) {
        ProcessDefinitionEntity definitionEntity = commandContext.getProcessEngineConfiguration().getDeploymentManager()
                .findDeployedProcessDefinitionById(processDefineId);

        if (StringUtils.isEmpty(taskId)) {
            throw new ActivitiException("task id can not be null");
        }

        TaskEntity task = commandContext.getTaskEntityManager().findTaskById(taskId);
        this.currentTaskAssignee = task.getAssignee();
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(task.getExecutionId());
        if (variables != null) {
            executionEntity.setVariables(variables);
        }

        ActivityImpl curActivity = definitionEntity.findActivity(task.getTaskDefinitionKey());
        // FlowNodeDetail next = getNextTaskNode(curActivity, executionEntity, commandContext);
        FlowNodeDetail next = findNextTaskNode(curActivity, executionEntity, commandContext);

        if (variables != null) {
            //移除掉variable
            executionEntity.removeVariables(variables.keySet());
        }

        return next;
    }

    /**
     * 获取判定下一步任务
     *
     * @param activity
     * @param execution
     * @param context
     * @return
     */
    protected FlowNodeDetail findNextTaskNode(ActivityImpl activity, ExecutionEntity execution, CommandContext context) {
        List<ActivityImpl> nextActivity = new ArrayList<>();
        performCorrectOutgoing(activity, execution, nextActivity);

        if (nextActivity.size() == 0) {
            logger.error("can not find the correct next flow step!");
            throw new ActivitiException("can not find the correct next flow step!");
        }

        if (nextActivity.size() > 1) {
            logger.error("流程配置错误！下一步骤存在多个符合条件的处理步骤，请联系管理员！");
            throw new ActivitiException("流程配置错误！下一步骤存在多个符合条件的处理步骤，请联系管理员！");
        }
        
        ActivityImpl nextAct = nextActivity.get(0);
        ActivityBehavior behavior = nextAct.getActivityBehavior();
        FlowNodeDetail nextNode = null;
        if (behavior instanceof UserTaskActivityBehavior || behavior instanceof MultiInstanceActivityBehavior) {
            TaskDefinition definition = (TaskDefinition) nextAct.getProperty("taskDefinition");
            nextNode = new FlowNodeDetail();
            nextNode.setNodeKey(definition.getKey());
            nextNode.setNodeName(definition.getNameExpression().getExpressionText());
            nextNode.setNodeType(nextAct.getProperty("type").toString());
            String mode = getUserTaskModel(nextAct);
            nextNode.setMode(mode);

            boolean isMultiInstance = behavior instanceof MultiInstanceActivityBehavior;
            nextNode.setJointNode(isMultiInstance);
            nextNode.setNodeCandidates(findTaskCandidateUserRange(definition, nextAct, context, execution, isMultiInstance));
        } else if (behavior instanceof NoneEndEventActivityBehavior) {
            nextNode = new FlowNodeDetail();
            nextNode.setNodeKey(nextAct.getId());
            nextNode.setNodeName(nextAct.getProperty("name") == null ? "" : nextAct.getProperty("name").toString());
            nextNode.setNodeType(nextAct.getProperty("type").toString());
        }

        return nextNode;
    }

    protected void performCorrectOutgoing(ActivityImpl activity, ExecutionEntity execution, List<ActivityImpl> nextActivity) {
        String defaultSequenceFlow = (String) activity.getProperty("default");
        List<PvmTransition> outTransition = activity.getOutgoingTransitions();
        for (PvmTransition transition : outTransition) {
            if (defaultSequenceFlow == null || !transition.getId().equals(defaultSequenceFlow)) {
                PvmActivity destination = transition.getDestination();
                ActivityImpl act = (ActivityImpl) destination;
                ActivityBehavior behavior = act.getActivityBehavior();
                if (behavior instanceof UserTaskActivityBehavior || behavior instanceof MultiInstanceActivityBehavior) {
                    // destination为userTask
                    Condition condition = (Condition) transition.getProperty(BpmnParse.PROPERTYNAME_CONDITION);
                    if (null == condition || condition.evaluate(transition.getId(), execution)) {
                        nextActivity.add(act);
                    }
                } else if (behavior instanceof ExclusiveGatewayActivityBehavior) {
                    // destination为排他网关，继续迭代
                    performCorrectOutgoing(act, execution, nextActivity);
                } else if (behavior instanceof NoneEndEventActivityBehavior) {
                    // destination 为endevent
                    Condition condition = (Condition) transition.getProperty(BpmnParse.PROPERTYNAME_CONDITION);
                    if (null == condition || condition.evaluate(transition.getId(), execution)) {
                        nextActivity.add(act);
                    }
                }
            }
        }

        if (nextActivity.size() == 0 && null != defaultSequenceFlow) {
            // 条件均不满足的情况下则选择默认流
            PvmTransition defalutTransition = activity.findOutgoingTransition(defaultSequenceFlow);
            if (null != defalutTransition) {
                ActivityImpl act = (ActivityImpl) defalutTransition.getDestination();
                ActivityBehavior behavior = act.getActivityBehavior();
                if (behavior instanceof UserTaskActivityBehavior || behavior instanceof MultiInstanceActivityBehavior
                        || behavior instanceof NoneEndEventActivityBehavior) {
                    // 默认流的处理无需判断Condition
                    nextActivity.add(act);
                } else if (behavior instanceof ExclusiveGatewayActivityBehavior) {
                    performCorrectOutgoing(act, execution, nextActivity);
                }
            }
        }
    }


    /**
     * 判断下一步
     *
     * @param activity
     * @param execution
     * @param context
     * @return
     * @deprecated 当存在默认流配置是，下一步的解析存在问题，请使用findNextTaskNode(ActivityImpl, ExecutionEntity, CommandContext)方法代替
     */
    @Deprecated
    protected FlowNodeDetail getNextTaskNode(ActivityImpl activity, ExecutionEntity execution, CommandContext context) {
        FlowNodeDetail nextNode = null;
        List<PvmTransition> outTransition = activity.getOutgoingTransitions();
        for (PvmTransition transition : outTransition) {
            PvmActivity destination = transition.getDestination();
            ActivityImpl act = (ActivityImpl) destination;
            ActivityBehavior behavior = act.getActivityBehavior();

            if (behavior instanceof UserTaskActivityBehavior || behavior instanceof MultiInstanceActivityBehavior) {
                Condition condition = (Condition) transition.getProperty(BpmnParse.PROPERTYNAME_CONDITION);
                if (null == condition || condition.evaluate(transition.getId(), execution)) {
                    TaskDefinition definition = (TaskDefinition) act.getProperty("taskDefinition");
                    nextNode = new FlowNodeDetail();
                    nextNode.setNodeKey(definition.getKey());
                    nextNode.setNodeName(definition.getNameExpression().getExpressionText());
                    nextNode.setNodeType(act.getProperty("type").toString());

                    String mode = getUserTaskModel(act);
                    nextNode.setMode(mode);
                    if (behavior instanceof MultiInstanceActivityBehavior) {
                        if (StringUtils.isNotEmpty(mode) && mode.equals("manual")) {
                            nextNode.setNodeCandidates(findTaskCandidateUserRange(definition, act, context, execution, true));
                        }
                        if (behavior instanceof ParallelMultiInstanceBehavior) {
                            nextNode.setJointNode(true);
                        }
                    } else {
                        if (StringUtils.isNotEmpty(mode) && mode.equals("manual")) {
                            nextNode.setNodeCandidates(findTaskCandidateUserRange(definition, act, context, execution, false));
                        }
                    }

                    return nextNode;
                }
            } else if (behavior instanceof ExclusiveGatewayActivityBehavior) {
                return getNextTaskNode(act, execution, context);
            } else if (behavior instanceof NoneEndEventActivityBehavior) {
                Condition condition = (Condition) transition.getProperty(BpmnParse.PROPERTYNAME_CONDITION);
                if (null == condition || condition.evaluate(transition.getId(), execution)) {
                    nextNode = new FlowNodeDetail();
                    nextNode.setNodeKey(act.getId());
                    nextNode.setNodeName(act.getProperty("name") == null ? "" : act.getProperty("name").toString());
                    nextNode.setNodeType(act.getProperty("type").toString());
                    return nextNode;
                }
            }
        }

        return null;
    }

    /**
     * 解析节点人员策略，查找人员范围
     *
     * @param task
     * @param act
     * @param commandContext
     * @param execution
     * @param isParallel
     * @return
     */
    protected Collection<MemberBasicInfoDto> findTaskCandidateUserRange(TaskDefinition task, ActivityImpl act, CommandContext commandContext, DelegateExecution execution, boolean isParallel) {
        Expression assigneeExpression = task.getAssigneeExpression();
        Set<Expression> candidateUserExpressions = task.getCandidateUserIdExpressions();       // 候选人
        Set<Expression> candidateGroupExpressions = task.getCandidateGroupIdExpressions();     // 候选角色
        CustomUserEntityManager manager = (CustomUserEntityManager) commandContext.getUserIdentityManager();
        Collection<String> candidateUsers = null;
        Collection<String> candidateGroups = null;

        // 固定处理人策略
        if (null != assigneeExpression && !isParallel) {
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
                    candidateUsers.addAll(extractCandidates(value.toString()));
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

        // 候选角色策略, 之后可能当做岗位策略来做
        if (null != candidateGroupExpressions && !candidateGroupExpressions.isEmpty()) {
            candidateGroups = new ArrayList<>();
            for (Expression groupIdExpr : candidateGroupExpressions) {
                Object value = groupIdExpr.getValue(execution);
                if (value instanceof String) {
                    candidateGroups.addAll(extractCandidates((String) value));
                } else if (value instanceof Collection) {
                    candidateGroups.addAll((Collection<String>) value);
                } else {
                    throw new ActivitiIllegalArgumentException("Expression did not resolve to a string or collection of strings");
                }
            }

            if (null != candidateGroups && candidateGroups.size() > 0) {
                List<MemberBasicInfoDto> userlist = manager.batchQueryMemberListByPosts(candidateGroups);
                return new HashSet<>(userlist);
            }
        }

        // 自定义的混合策略
        Object userStrategy = act.getProperty(ExtendFlowElementConstant.EXTEND_USER_STRATEGY_NAMESPACE);
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
                    HistoricProcessInstance historyInstance = commandContext.getHistoricProcessInstanceEntityManager()
                            .findHistoricProcessInstance(execution.getProcessInstanceId());
                    String applyUserCode = historyInstance.getStartUserId();    // 获取发起人
                    strategy = new ApplyRelateStrategy(manager, ExtendFlowElementConstant.userStrategyTypeMapper.get(type), applyUserCode);
                } else if (ArrayUtils.contains(ExtendFlowElementConstant.prevStepHandlerRelateType, type)) {
                    // 上一步骤处理人运行时相关特殊策略
                    strategy = new PrevHandlerRelateStrategy(manager, ExtendFlowElementConstant.userStrategyTypeMapper.get(type), currentTaskAssignee);
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

    /**
     * 解析用户任务扩展的模式属性
     *
     * @param activity
     * @return
     */
    protected String getUserTaskModel(ActivityImpl activity) {
        String mode = null;
        String type = (String) activity.getProperty("type");
        if (type.equals("userTask")) {
            Object obj = activity.getProperty(ExtendFlowElementConstant.EXTEND_MODEL_NAMESPACE);
            if (null != obj && obj instanceof HashMap) {
                Map map = (HashMap) obj;
                mode = MapUtils.getString(map, ExtendFlowElementConstant.EXTEND_DEFAULT_KEY, null);
            }
        }

        return mode;
    }

    /**
     * 分隔符处理
     *
     * @param str
     * @return
     */
    protected List<String> extractCandidates(String str) {
        return Arrays.asList(str.split("[\\s]*,[\\s]*"));
    }

}
