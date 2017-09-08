package com.xuanwu.flowengine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xuanwu.flowengine.cmd.AutoJumpTaskCmd;
import com.xuanwu.flowengine.cmd.GetFlowNodeDetailCmd;
import com.xuanwu.flowengine.cmd.PredictNextStepCmd;
import com.xuanwu.flowengine.entity.FallBackNode;
import com.xuanwu.flowengine.entity.FlowNodeDetail;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.dto.FlowMessageParam;
import com.xuanwu.flowengine.extend.CustomBpmnJsonConverter;
import com.xuanwu.flowengine.mapper.execution.CurrentTaskInfoExecution;
import com.xuanwu.flowengine.util.WorkflowUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.cmd.ExecuteCustomSqlCmd;
import org.activiti.engine.impl.persistence.entity.ModelEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程通用服务类
 * File created on 2017/3/28.
 *
 * @author jkun
 */
public class ProcessCoreService {

    protected RepositoryService repositoryService;
    protected RuntimeService runtimeService;
    protected TaskService taskService;
    protected HistoryService historyService;
    protected ManagementService managementService;
    protected IdentityService identityService;

    public ProcessCoreService(ProcessEngineConfiguration configuration) {
        this.repositoryService = configuration.getRepositoryService();
        this.runtimeService = configuration.getRuntimeService();
        this.taskService = configuration.getTaskService();
        this.historyService = configuration.getHistoryService();
        this.managementService = configuration.getManagementService();
        this.identityService = configuration.getIdentityService();
    }

    /**
     * 部署流程
     *
     * @param deployName 流程部署名
     * @param resources  流程资源（可以为多个）
     * @return
     */
    public Deployment deployResources(String deployName, String... resources) {
        DeploymentBuilder builder = repositoryService.createDeployment();
        for (String item : resources) {
            if (StringUtils.isNotEmpty(item)) {
                builder.addClasspathResource(item);
            }
        }

        Deployment deployment = builder.name(deployName).deploy();
        return deployment;
    }

    /**
     * 部署流程
     *
     * @param name            部署名
     * @param resourceContent 流程定义内容
     * @return 流程部署对象
     */
    public Deployment deployByResourceString(String name, String resourceContent) {
        name = WorkflowUtils.handleProcessName(name);
        Deployment deployment = repositoryService.createDeployment().name(name).addString(name, resourceContent).deploy();
        return deployment;
    }

    /**
     * 部署流程
     *
     * @param name      部署名
     * @param bpmnModel Bpmn模型对象
     * @return
     */
    public Deployment deployByBpmnModel(String name, BpmnModel bpmnModel) {
        String resourceName = WorkflowUtils.handleProcessName(name);
        Deployment deployment = repositoryService.createDeployment().name(name).addBpmnModel(resourceName, bpmnModel).deploy();
        return deployment;
    }

    /**
     * 获取可以退回的节点
     *
     * @param taskKey
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    public List<FallBackNode> findBackActivity(String taskKey, String processInstanceId) throws Exception {
        Map<String, ActivityImpl> backActivityMap = new HashMap<>();
        List<FallBackNode> fallBackNodes = new ArrayList<FallBackNode>();

        ProcessInstance instance = findProcessInstanceById(processInstanceId);
        ProcessDefinitionEntity defineEntity = findProcessDefinitionEntityById(instance.getProcessDefinitionId());
        ActivityImpl curActivity = defineEntity.findActivity(taskKey);
        if (!checkIsJointTask(curActivity)) {
            recurseBackActivity(instance, curActivity, backActivityMap, fallBackNodes);
        }

        return fallBackNodes;
    }

    /**
     * 递归获取可退回的节点
     *
     * @param instance
     * @param curActivity
     * @param backList
     * @return
     * @throws Exception
     */
    private void recurseBackActivity(ProcessInstance instance, ActivityImpl curActivity, Map<String, ActivityImpl> backList, List<FallBackNode> fallBackNodes) throws Exception {
        List<ActivityImpl> tempList = findPreIncomingTaskActivity(curActivity);   // 获取指定节点的入口节点信息

        // 过滤未流转过的入口节点
        for (ActivityImpl item : tempList) {
            HistoricActivityInstance activityInstance = filterNearestHappendActivity(instance, item);
            if (null != activityInstance) {
                if (!backList.containsKey(item.getId())) {
                    backList.put(item.getId(), item);
                    FallBackNode fallbackNode = new FallBackNode();
                    fallbackNode.setNodeKey(item.getId());
                    fallbackNode.setNodeName(item.getProperty("name").toString());
                    fallBackNodes.add(fallbackNode);

                    recurseBackActivity(instance, item, backList, fallBackNodes);
                }
            }
        }
    }

    /**
     * 获取最近的入口任务集合
     *
     * @param activity
     * @return
     */
    private List<ActivityImpl> findPreIncomingTaskActivity(ActivityImpl activity) {
        List<PvmTransition> inTransitions = activity.getIncomingTransitions();
        List<ActivityImpl> nearest = new ArrayList<ActivityImpl>();

        for (PvmTransition transition : inTransitions) {
            ActivityImpl source = (ActivityImpl) transition.getSource();
            String type = (String) source.getProperty("type");

            if (type.equals("userTask")) {
                nearest.add(source);
            } else if (type.equals("exclusiveGateway")) {
                nearest.addAll(findPreIncomingTaskActivity(source));
            } else if (type.equals("parallelGateway")) {
                nearest.addAll(findPreIncomingTaskActivity(source));
            }
        }

        return nearest;
    }

    /**
     * 判断是否为会审节点
     *
     * @param activity
     * @return
     */
    public boolean checkIsJointTask(ActivityImpl activity) {
        ActivityBehavior behavior = activity.getActivityBehavior();
        if (behavior instanceof MultiInstanceActivityBehavior) {
            return true;
        }

        return false;
    }

    /**
     * 根据任务Id和活动Id获取已结束的最近发生的活动实例
     *
     * @param instance
     * @param activity
     * @return
     */
    private HistoricActivityInstance filterNearestHappendActivity(ProcessInstance instance, ActivityImpl activity) {
        List<HistoricActivityInstance> historics = historyService.createHistoricActivityInstanceQuery().processInstanceId(instance.getProcessInstanceId())
                .activityId(activity.getId()).finished().orderByHistoricActivityInstanceEndTime().desc().list();
        if (null != historics && historics.size() > 0) {
            return historics.get(0);
        }

        return null;
    }


    /**
     * 流程转向（用于流程中止和退回）
     *
     * @param taskId
     * @param targetActId
     * @param variables
     * @throws Exception
     */
    public void turnActivity(String taskId, String targetActId, Map<String, Object> variables) throws Exception {
        ActivityImpl curActivity = findActivityImpl(taskId, null);
        //清空流向
        List<PvmTransition> tempOutTransitions = clearOutTransition(curActivity);

        try {
            //创建新的流向
            TransitionImpl newTransition = curActivity.createOutgoingTransition();
            ActivityImpl targetActivity = findActivityImpl(taskId, targetActId);
            newTransition.setDestination(targetActivity);
            //提交任务
            taskService.complete(taskId, variables);
            targetActivity.getIncomingTransitions().remove(newTransition);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            //还原流向
            restoreDefaultTransition(curActivity, tempOutTransitions);
        }
    }

    /**
     * 清空流向
     *
     * @param activity
     * @return
     */
    private List<PvmTransition> clearOutTransition(ActivityImpl activity) {
        List<PvmTransition> tempTransitions = new ArrayList<PvmTransition>();
        List<PvmTransition> outTransitions = activity.getOutgoingTransitions();

        for (PvmTransition transition : outTransitions) {
            tempTransitions.add(transition);
        }
        outTransitions.clear();

        return tempTransitions;
    }

    /**
     * 还原默认流向
     *
     * @param activity
     * @param transitions
     */
    private void restoreDefaultTransition(ActivityImpl activity, List<PvmTransition> transitions) {
        List<PvmTransition> outTransition = activity.getOutgoingTransitions();
        outTransition.clear();
        outTransition.addAll(transitions);
    }

    /**
     * 根据流程部署Id和资源名称获取资源流
     *
     * @param deployId
     * @param resourceName
     * @return
     * @throws Exception
     */
    public InputStream findProcessResourceStreamByName(String deployId, String resourceName) throws Exception {
        InputStream stream = repositoryService.getResourceAsStream(deployId, resourceName);
        if (null == stream) {
            String error = String.format("can't find the process resource, resourcename: %s , deployid: %s", resourceName, deployId);
            throw new ActivitiObjectNotFoundException(error);
        }

        return stream;
    }

    /**
     * 根据流程定义Id获取流程定义
     *
     * @param processDefineId
     * @return
     * @throws Exception
     */
    public ProcessDefinitionEntity findProcessDefinitionEntityById(String processDefineId) throws Exception {
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefineId);
        if (null == processDefinitionEntity) {
            throw new ActivitiObjectNotFoundException("can't find the process definition, processdefineid: " + processDefineId, ProcessDefinitionEntity.class);
        }

        return processDefinitionEntity;
    }

    /**
     * 根据任务实例Id获取流程定义
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) throws Exception {
        ProcessDefinitionEntity processDefinitionEntity = findProcessDefinitionEntityById(findTaskEntityById(taskId).getProcessDefinitionId());
        return processDefinitionEntity;
    }

    /**
     * 根据流程实例Id获取流程定义
     *
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    public ProcessDefinitionEntity findProcessDefinitionEntityByProcessInstanceId(String processInstanceId) throws Exception {
        ProcessDefinitionEntity processDefinitionEntity = findProcessDefinitionEntityById(findProcessInstanceById(processInstanceId).getProcessDefinitionId());
        return processDefinitionEntity;
    }

    /**
     * 根据流程定义Key获取最新版本的流程定义
     *
     * @param processDefineKey
     * @return
     * @throws Exception
     */
    public ProcessDefinitionEntity findProcessDefinitionEntityByProcessDefineKey(String processDefineKey) throws Exception {
        //默认获取最新版本的流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefineKey).latestVersion().active().singleResult();
        if (null == processDefinition) {
            throw new ActivitiObjectNotFoundException("can't find the latest process definition, processdefinekey: " + processDefineKey, ProcessDefinitionEntity.class);
        }

        return findProcessDefinitionEntityById(processDefinition.getId());
    }

    /**
     * 根据任务实例Id获取任务实例信息
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    public TaskEntity findTaskEntityById(String taskId) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            throw new ActivitiObjectNotFoundException("can't find the task instance, taskid: " + taskId, TaskEntity.class);
        }

        return task;
    }

    /**
     * 根据流程实例Id获取流程实例信息
     *
     * @param processInstanceId
     * @return
     */
    public ProcessInstance findProcessInstanceById(String processInstanceId) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null == instance) {
            throw new ActivitiObjectNotFoundException("can't find the process instance, instanceid: " + processInstanceId, ProcessInstance.class);
        }

        return instance;
    }

    /**
     * 根据任务实例Id获取流程实例信息
     *
     * @param taskId
     * @return
     * @throws Exception
     */
    public ProcessInstance findProcessInstanceByTaskId(String taskId) throws Exception {
        ProcessInstance instance = findProcessInstanceById(findTaskEntityById(taskId).getProcessInstanceId());
        return instance;
    }

    /**
     * 根据任务Id和活动定义Id获取活动定义
     *
     * @param taskId
     * @param actId
     * @return
     * @throws Exception
     */
    public ActivityImpl findActivityImpl(String taskId, String actId) throws Exception {
        ProcessDefinitionEntity processDefinitionEntity = findProcessDefinitionEntityByTaskId(taskId);
        if (StringUtils.isEmpty(actId)) {
            actId = findTaskEntityById(taskId).getTaskDefinitionKey();
        }

        if (actId.toLowerCase().equals("__end")) {
            //结束节点
            for (ActivityImpl activity : processDefinitionEntity.getActivities()) {
                List<PvmTransition> outTransitions = activity.getOutgoingTransitions();
                if (null == outTransitions || outTransitions.isEmpty()) {
                    return activity;
                }
            }
        } else if (actId.toLowerCase().equals("__start")) {
            //起始节点
            for (ActivityImpl activity : processDefinitionEntity.getActivities()) {
                List<PvmTransition> inTransitions = activity.getIncomingTransitions();
                if (null == inTransitions || inTransitions.isEmpty()) {
                    return activity;
                }
            }
        }

        ActivityImpl activityImpl = processDefinitionEntity.findActivity(actId);
        if (null == activityImpl) {
            throw new ActivitiObjectNotFoundException(String.format("can't find the activity define, taskid: %s, actid: %s", taskId, actId), ActivityImpl.class);
        }

        return activityImpl;
    }

    /**
     * 根据流程Id获取历史流程实例
     *
     * @param processInstanceId
     * @return
     */
    public HistoricProcessInstance findHistoricProcessInstanceById(String processInstanceId) {
        HistoricProcessInstance historicInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        return historicInstance;
    }

    /**
     * 根据任务实例Id和流程实例Id获取任务实例历史信息
     *
     * @param taskId
     * @param processInstanceId
     * @return
     */
    public HistoricTaskInstance findHistoricTaskInstanceByTaskIdAndInstanceId(String taskId, String processInstanceId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .taskId(taskId).singleResult();
        return historicTaskInstance;
    }

    /**
     * 自定义的获取用户任务实例的信息
     *
     * @param taskId
     * @param processInstanceId
     * @return
     */
    public HistoricTaskInstance findHistoricTaskDetail(String taskId, String processInstanceId) {
        String nativeSql = "select ht.id_, ht.proc_def_id_, ht.task_def_key_, ht.proc_inst_id_, ht.name_, ht.start_time_, ht.end_time_, ht.assignee_, tr.jumpintype delete_reason  \n" +
                "from act_hi_taskinst as ht\n" +
                "left join act_taskroute as tr \n" +
                "on ht.id_ = tr.taskinstanceid and ht.proc_inst_id_ = tr.processinstanceid\n" +
                "where ht.id_ = #{taskid} and ht.proc_inst_id_ = #{instanceid}\n" +
                "limit 1\n";

        HistoricTaskInstance historicTaskInstance = historyService.createNativeHistoricTaskInstanceQuery().sql(nativeSql).parameter("taskid", taskId)
                .parameter("instanceid", processInstanceId).singleResult();
        return historicTaskInstance;
    }

    /**
     * 根据人员Id和流程实例Id获取流程历史活动信息，按活动创建时间的降序排列
     *
     * @param userCode
     * @param processInstanceId
     * @return
     */
    public List<HistoricActivityInstance> findHistoricActivityByUserCodeAndInstanceId(String userCode, String processInstanceId) {
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).taskAssignee(userCode)
                .orderByHistoricActivityInstanceStartTime().desc().list();
        return activityInstances;
    }

    /**
     * 预测下一节点信息
     *
     * @param processDefineId
     * @param taskId
     * @param params
     * @return
     */
    public FlowNodeDetail predictNextNode(String processDefineId, String taskId, Map<String, Object> params) {
        FlowNodeDetail flowNodeDetail = managementService.executeCommand(new PredictNextStepCmd(processDefineId, taskId, params));
        return flowNodeDetail;
    }

    /**
     * 调用自定义的自由跳转命令
     *
     * @param taskId
     * @param source
     * @param target
     * @param variables
     * @param reason
     * @throws Exception
     */
    public void invokeAutoJumpTaskCmd(String taskId, ActivityImpl source, ActivityImpl target, Map<String, Object> variables, String reason) throws Exception {
        TaskEntity taskEntity = findTaskEntityById(taskId);
        managementService.executeCommand(new AutoJumpTaskCmd(taskEntity.getExecutionId(), source, target, variables, reason));
    }

    /**
     * 根据流程实例Id获取流程当前的任务实例状态 (用于提供审批消息的参数域)
     *
     * @param processInstanceId
     * @return
     */
    public FlowMessageParam getCurrentTaskInfoByProcInstanceId(String processInstanceId) {
        CurrentTaskInfoExecution execution = new CurrentTaskInfoExecution(processInstanceId);
        FlowMessageParam result = managementService.executeCommand(new ExecuteCustomSqlCmd<>(execution.getMapperClass(), execution));
        return result;
    }


    /**
     * 获取已办的历史流程实例
     *
     * @param userCode
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<HistoricProcessInstance> findHaveDoneHistoricInstance(String userCode, int pageIndex, int pageSize) {
        final String nativeSqlCount = "select count(distinct t1.proc_inst_id_) from act_hi_procinst t1 inner join act_hi_actinst t2 on t1.proc_inst_id_ = t2.proc_inst_id_\n" +
                "where (t2.assignee_ = #{userCode} and t2.end_time_ is not null) or t1.start_user_id_ = #{userCode}";
        final String nativeSql = "select distinct t1.* from act_hi_procinst t1 inner join act_hi_actinst t2 on t1.proc_inst_id_ = t2.proc_inst_id_\n" +
                "where (t2.assignee_ = #{userCode} and t2.end_time_ is not null) or t1.start_user_id_ = #{userCode}\n" +
                "order by t1.start_time_ desc, t1.end_time_ desc";

        long recordCount = historyService.createNativeHistoricProcessInstanceQuery().sql(nativeSqlCount).parameter("userCode", userCode).count();
        if (recordCount == 0) {
            return new Page<>(pageSize, 0, null);
        }

        int offset = (pageIndex - 1) * pageSize;
        List<HistoricProcessInstance> instances = historyService.createNativeHistoricProcessInstanceQuery().sql(nativeSql)
                .parameter("userCode", userCode).listPage(offset, pageSize);
        return new Page<>(pageSize, recordCount, instances);
    }

    /**
     * 获取个人的待办任务分页信息
     *
     * @param userCode
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<Task> findMyToDoTasks(String userCode, int pageIndex, int pageSize) {
        String nativeSqlCount = "select count(distinct t.id_) from act_ru_task t\n" +
                "left join act_ru_identitylink i\n" +
                "on t.id_ = i.task_id_\n" +
                "where (t.assignee_ = #{userid} or (t.assignee_ is null and i.type_ = 'candidate' and (i.user_id_ = #{userid} %s)))";
        String nativeSql = "select distinct t.* from act_ru_task t\n" +
                "left join act_ru_identitylink i\n" +
                "on t.id_ = i.task_id_\n" +
                "where (t.assignee_ = #{userid} or (t.assignee_ is null and i.type_ = 'candidate' and (i.user_id_ = #{userid} %s)))\n" +
                "order by t.create_time_ desc\n";

        List<Group> groups = identityService.createGroupQuery().groupMember(userCode).list();
        StringBuilder sb = new StringBuilder();
        String groupIds = null;

        if (null != groups && groups.size() > 0) {
            for (Group item : groups) {
                sb.append(String.format("'%s',", item.getId()));
            }

            groupIds = sb.toString().substring(0, sb.toString().length() - 1);
            nativeSqlCount = String.format(nativeSqlCount, " or i.group_id_ in (#{groupids})");
            nativeSql = String.format(nativeSql, " or i.group_id_ in (#{groupids})");
        } else {
            nativeSqlCount = String.format(nativeSqlCount, "");
            nativeSql = String.format(nativeSql, "");
        }

        NativeTaskQuery taskQuery = taskService.createNativeTaskQuery().sql(nativeSqlCount).parameter("userid", userCode);
        if (groupIds != null) {
            taskQuery.parameter("groupids", groupIds);
        }

        long recordCount = taskQuery.count();
        if (recordCount == 0) {
            return new Page<>(pageSize, 0, null);
        }

        int offset = (pageIndex - 1) * pageSize;
        List<Task> tasks = taskQuery.sql(nativeSql).listPage(offset, pageSize);
        return new Page<>(pageSize, recordCount, tasks);
    }

    /**
     * 获取节点详情
     *
     * @param taskKey
     * @param processDefineId
     * @param processInstanceId
     * @param prevHandler
     * @return
     */
    public FlowNodeDetail getFlowNodeDetail(String taskKey, String processDefineId, String processInstanceId, String prevHandler) {
        GetFlowNodeDetailCmd command = new GetFlowNodeDetailCmd(taskKey, processInstanceId, processDefineId, prevHandler);
        return managementService.executeCommand(command);
    }

    public List<String> getProcessList() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().active().latestVersion().list();
        List<String> result = list.stream().map(item -> item.getId()).collect(Collectors.toList());
        return result;
    }

    /**
     * 流程列表参数化查询
     *
     * @param queryParam
     * @return
     */
    public List<ProcessDefinition> getProcessList(Map<String, Object> queryParam) {
        String baseSql = "select id_, name_, key_, version_, resource_name_ from\n" +
                "(\n" +
                "   select rank() over (partition by ap.key_ order by ap.version_ desc) as ranknum , ap.* \n" +
                "\t from act_re_procdef ap\n" +
                "\t inner join act_re_model am on ap.deployment_id_ = am.deployment_id_\n" +
                "   inner join act_flowrelation af on am.id_ = af.flowmodelcode\n" +
                "\t inner join act_flowcategory ac on af.flowcategorycode = ac.flowcategorycode\n" +
                "\t where ap.suspension_state_ = 1 {0} \n" +
                ") a\n" +
                "where a.ranknum = 1 ";

        // 处理查询参数
        StringBuilder sb = new StringBuilder();
        if (null != queryParam) {
            if (StringUtils.isNotEmpty(MapUtils.getString(queryParam, "processname"))) {
                sb.append(" and ap.name_ ilike '%' || #{processname} || '%'");
            }
            if (StringUtils.isNotEmpty(MapUtils.getString(queryParam, "processkey"))) {
                sb.append(" and ap.key_ = #{processkey}");
            }
            if (StringUtils.isNotEmpty(MapUtils.getString(queryParam, "flowcategoryname"))) {
                sb.append(" and ac.flowcategoryname ilike '%' || #{flowcategoryname} || '%'");
            }
        }

        // 处理参数和nativeQuery对象
        baseSql = MessageFormat.format(baseSql, sb.toString());
        NativeProcessDefinitionQuery nativeQuery = repositoryService.createNativeProcessDefinitionQuery();
        nativeQuery.sql(baseSql);

        if (null != queryParam) {
            for (String item : queryParam.keySet()) {
                nativeQuery.parameter(item, queryParam.get(item));
            }
        }

        return nativeQuery.list();
    }

    /**
     * 创建模型
     *
     * @param name
     * @param category
     * @return
     */
    public Model createModel(String name, Long category, String key, String description) throws UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode editNode = mapper.createObjectNode();
        editNode.put("id", "canvas");
        editNode.put("resourceId", "canvas");
        ObjectNode stencilset = mapper.createObjectNode();
        stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editNode.set("stencilset", stencilset);

        Model model = repositoryService.newModel();
        model.setName(name);
        model.setCategory(String.valueOf(category));
        model.setKey(StringUtils.defaultString(key));
        //model.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(StringUtils.defaultString(key)).count() + 1)));
        description = StringUtils.defaultString(description);

        ObjectNode modelObjectNode = mapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, model.getVersion());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        model.setMetaInfo(modelObjectNode.toString());

        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(), editNode.toString().getBytes("utf-8"));
        return model;
    }

    /**
     * 删除流程模型
     *
     * @param modelId
     */
    public void deleteModel(String modelId) {
        if (StringUtils.isEmpty(modelId)) {
            throw new ActivitiIllegalArgumentException("modelId不能为空！");
        }

        repositoryService.deleteModel(modelId);
    }

    /**
     * 更新模型
     *
     * @param model
     */
    public void updateModel(Model model) {
        repositoryService.saveModel(model);
    }

    /**
     * 根据模型编码获取模型
     *
     * @param modelId
     * @return
     */
    public Model getModel(String modelId) {
        if (StringUtils.isEmpty(modelId)) {
            throw new ActivitiIllegalArgumentException("modelId不能为空！");
        }

        return repositoryService.getModel(modelId);
    }

    /**
     * 通过模型编码来部署流程
     *
     * @param modelId
     * @return
     * @throws IOException
     */
    public Deployment deployModel(String modelId) throws IOException {
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            return null;
        }

        JsonNode modelNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
        CustomBpmnJsonConverter jsonConverter = new CustomBpmnJsonConverter();
        jsonConverter.changeDefaultConverter();
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(modelNode);
        String processName = model.getName();

        Deployment deployment = deployByBpmnModel(processName, bpmnModel);
        if (null != deployment) {
            // 更新model表的deploy_id字段，用于关联model和流程
            model.setDeploymentId(deployment.getId());
            // 部署流程后将version字段设置的和reversion一致，用于区分"已发布"和"已修改、待重新发布"
            model.setVersion(((ModelEntity) model).getRevisionNext());
            repositoryService.saveModel(model);
        }

        return deployment;
    }
}
