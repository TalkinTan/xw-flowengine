package com.xuanwu.flowengine.service;

//import com.xuanwu.apaas.core.utils.JsonUtil;
import com.xuanwu.flowengine.entity.dto.FlowMessageParam;
import com.xuanwu.flowengine.entity.dto.ProcessDefineDto;
import com.xuanwu.flowengine.entity.dto.TaskBasic;
import com.xuanwu.flowengine.extend.CustomProcessEngine;
import com.xuanwu.flowengine.mapper.execution.CustomTaskListExecution;
import com.xuanwu.flowengine.util.DateConverter;
import com.xuanwu.flowengine.util.JsonUtil;
import com.xuanwu.flowengine.entity.*;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.runtime.ProcessInstanceBuilderImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

import static com.xuanwu.flowengine.entity.ProcessChoice.CHOICENAME_VARIABLE_KEY;
import static com.xuanwu.flowengine.entity.ProcessChoice.CHOICE_VARIABLE_KEY;

/**
 * 流程服务对外API接口
 * File created on 2017/3/31.
 *
 * @author jkun
 */
public class WorkflowService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowService.class);

    protected ProcessEngine processEngine;
    protected ProcessCoreService processCoreService;
    protected TaskService taskService;
    protected FlowCategoryService flowCategoryService;
    protected FlowRelationService flowRelationService;

    private DateConverter dateConverter = new DateConverter();

    public WorkflowService(ProcessEngine processEngine) {
        this.processEngine = processEngine;
        initCommonSerivce();
    }

    private void initCommonSerivce() {
        this.processCoreService = ((CustomProcessEngine) processEngine).getProcessCoreService();
        this.flowCategoryService = ((CustomProcessEngine) processEngine).getFlowCategoryService();
        this.flowRelationService = ((CustomProcessEngine) processEngine).getFlowRelationService();
        this.taskService = processEngine.getTaskService();
    }

    /**
     * 流程部署接口
     *
     * @param deployName        部署名
     * @param resourcePath      流程定义xml资源路径
     * @param resourceImagePath 流程图片资源路径，可以为null
     * @return
     */
    public Deployment deploymentProcess(String deployName, String resourcePath, String resourceImagePath) {
        Deployment deployment = processCoreService.deployResources(deployName, resourcePath, resourceImagePath);
        return deployment;
    }

    /**
     * 流程部署接口重载
     *
     * @param name            流程资源名称
     * @param resourceContent 流程资源内容字符串
     * @return
     */
    public Deployment deploymentProcessByResourceString(String name, String resourceContent) {
        Deployment deployment = processCoreService.deployByResourceString(name, resourceContent);
        return deployment;
    }

    public List<String> getAllProcessList() {
        List<String> processList = processCoreService.getProcessList();
        return processList;
    }

    /**
     * 查询流程列表
     *
     * @param queryParam
     * @return
     */
    public List<ProcessDefinition> getAllProcessList(Map<String, Object> queryParam) {
        return processCoreService.getProcessList(queryParam);
    }

    //region 流程配置接口

    /**
     * 流程配置接口：根据流程定义Id获取流程定义基本信息
     *
     * @param processDefineId
     * @return
     * @throws Exception
     */
    public ProcessDefineDto getProcessDefineInfo(String processDefineId) throws Exception {
        ProcessDefinitionEntity definitionEntity = processCoreService.findProcessDefinitionEntityById(processDefineId);
        InputStream stream = processCoreService.findProcessResourceStreamByName(definitionEntity.getDeploymentId(), definitionEntity.getResourceName());
        String resourceStr = IOUtils.toString(stream, Charset.forName("utf-8"));
        ProcessDefineDto dto = new ProcessDefineDto();
        dto.setProcessDefineId(definitionEntity.getId());
        dto.setProcessDefineKey(definitionEntity.getKey());
        dto.setProcessDefineName(definitionEntity.getName());
        dto.setResource(resourceStr);

        return dto;
    }

    /**
     * 流程配置接口: 根据流程定义Id获取流程资源图片流
     *
     * @param processDefineId
     * @return
     * @throws Exception
     */
    public InputStream getProcessDiagramStream(String processDefineId) throws Exception {
        ProcessDefinitionEntity definitionEntity = processCoreService.findProcessDefinitionEntityById(processDefineId);
        if (StringUtils.isEmpty(definitionEntity.getDiagramResourceName())) {
            // 数据库无资源图片，自动布局生成
            BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(definitionEntity.getId());
            new BpmnAutoLayout(bpmnModel).execute();
            ProcessDiagramGenerator generator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            return generator.generateDiagram(bpmnModel, "png", Collections.emptyList(), Collections.emptyList(), "黑体", "黑体", "黑体", null, 1.0);
        }

        return processCoreService.findProcessResourceStreamByName(definitionEntity.getDeploymentId(), definitionEntity.getDiagramResourceName());
    }

    /**
     * 流程配置接口: 创建流程模型
     *
     * @param modelName
     * @param categoryCode
     * @param modelKey
     * @param description
     * @return
     * @throws Exception
     */
    public Model createModel(String modelName, Long categoryCode, String modelKey, String description) throws Exception {
        return processCoreService.createModel(modelName, categoryCode, modelKey, description);
    }

    /**
     * 流程配置接口: 删除流程模型
     *
     * @param modelId
     * @throws Exception
     */
    public void deleteModel(String modelId) {
        processCoreService.deleteModel(modelId);
    }

    /**
     * 流程配置接口: 更新流程模型
     *
     * @param entity
     */
    public void updateModel(Model entity) {
        processCoreService.updateModel(entity);
    }

    /**
     * 流程配置接口: 根据模型Id获取流程模型
     *
     * @param modelId
     * @return
     */
    public Model getModel(String modelId) {
        return processCoreService.getModel(modelId);
    }

    /**
     * 流程配置接口: 部署模型
     *
     * @param modelId
     * @return
     * @throws IOException
     */
    public Deployment deployModel(String modelId) throws IOException {
        return processCoreService.deployModel(modelId);
    }

    /**
     * 停用流程
     *
     * @param processDefineId
     */
    public void suspendProcess(String processDefineId) {
        processEngine.getRepositoryService().suspendProcessDefinitionById(processDefineId);
    }

    /**
     * 启用流程
     *
     * @param processDefineId
     */
    public void activeProcess(String processDefineId) {
        processEngine.getRepositoryService().activateProcessDefinitionById(processDefineId);
    }

    //endregion

    //region 流程运行接口

    /**
     * 预发起流程
     *
     * @param processDefineKey 流程定义Key
     * @return 流程定义包装对象
     */
    public ProcessDefineWrapper preAllocateFlow(String processDefineKey) throws Exception {
        ProcessDefinitionEntity definitionEntity = processCoreService.findProcessDefinitionEntityByProcessDefineKey(processDefineKey);
        ExtensionElementService service = new ExtensionElementService(definitionEntity);
        ProcessDefineWrapper wrapper = new ProcessDefineWrapper();
        wrapper.setProcessDefineId(definitionEntity.getId());
        wrapper.setProcessDefineName(definitionEntity.getName());
        wrapper.setProcessDefineKey(definitionEntity.getKey());
        wrapper.setUiConfig(service.getActivityRelateUIConfig(ExtendFlowElementConstant.EXTEND_FIRSTSTEP_ID));
        wrapper.setInstanceNameRule(service.getProcessInstanceNameRule());

        return wrapper;
    }

    /**
     * 流程发起
     *
     * @param applyMemberCode
     * @param variables
     * @return
     * @throws Exception
     */
    public ProcessApplyResult allocateFlow(String applyMemberCode, Map<String, Object> variables) throws Exception {
        // 设置发起人
        Authentication.setAuthenticatedUserId(applyMemberCode);
        // 解析流程入参相关数据
        Map processParam = MapUtils.getMap(variables, ExtendFlowElementConstant.PROCESS_PARAM_OBJ_KEY, null);
        if (null == processParam) {
            throw new ActivitiException("流程入参对象__approvaldata不能为空!");
        }

        String jsonContent = JsonUtil.serialize(processParam);
        ProcessAllocateParam allocateParam = JsonUtil.deserialize(jsonContent, ProcessAllocateParam.class);
        if (null == allocateParam) {
            throw new ActivitiException("流程入参对象解析失败!");
        }

        if (StringUtils.isEmpty(allocateParam.getBusinessObjName())) {
            throw new ActivitiException("获取业务对象名称失败!");
        }
        Map<String, Object> bizData = (Map<String, Object>) MapUtils.getMap(variables, allocateParam.getBusinessObjName());
        if (null == bizData) {
            throw new ActivitiException("业务数据对象" + allocateParam.getBusinessObjName() + "不能为空!");
        }

        if (StringUtils.isEmpty(allocateParam.getBusinessObjPropertyName())) {
            throw new ActivitiException("获取业务对象标识属性名称失败!");
        }
        String businessKeyValue = MapUtils.getString(bizData, allocateParam.getBusinessObjPropertyName());
        if (StringUtils.isEmpty(businessKeyValue)) {
            throw new ActivitiException("业务数据标识属性值不能为空!");
        }

        // 移除掉传递的流程对象，剩余的即为业务对象
        variables.remove(ExtendFlowElementConstant.PROCESS_PARAM_OBJ_KEY);
        return allocateFlow(allocateParam.getProcessDefineKey(), applyMemberCode, businessKeyValue, allocateParam.getProcessInstanceName(), variables);
    }

    /**
     * 发起流程
     *
     * @param processDefineKey 流程定义Key
     * @param applyUserCode    发起人编码
     * @param businessKey      业务主键Id
     * @param instanceName     流程实例名称
     * @param variables        变量
     * @return
     */
    private ProcessApplyResult allocateFlow(String processDefineKey, String applyUserCode, String businessKey, String instanceName, Map<String, Object> variables) {
        //需在发起流程前设置发起人信息
        Authentication.setAuthenticatedUserId(applyUserCode);
        ProcessInstanceBuilderImpl instanceBuilder = new ProcessInstanceBuilderImpl((RuntimeServiceImpl) processEngine.getRuntimeService());
        instanceBuilder.processDefinitionKey(processDefineKey).businessKey(businessKey).processInstanceName(instanceName);
        if (null != variables) {
            for (String item : variables.keySet()) {
                instanceBuilder.addVariable(item, variables.get(item));
            }
        }

        ProcessInstance instance = instanceBuilder.start();
        ExecutionEntity execution = (ExecutionEntity) instance;
        ProcessApplyResult applyResult = new ProcessApplyResult();
        applyResult.setProcessDefineId(execution.getProcessDefinitionId());
        applyResult.setProcessInstanceId(execution.getProcessInstanceId());
        applyResult.setBusinessKey(execution.getBusinessKey());

        List<TaskEntity> tasks = execution.getTasks();
        if (null != tasks && tasks.size() > 0) {
            // 获取默认的第一步节点的任务实例Id
            if (tasks.get(0).getTaskDefinitionKey().equals(ExtendFlowElementConstant.EXTEND_FIRSTSTEP_ID)) {
                applyResult.setCurrentTaskId(tasks.get(0).getId());
                applyResult.setCurrentTaskName(tasks.get(0).getName());
            }
        }

        return applyResult;
    }

    /**
     * 预处理获取流程下一步信息
     *
     * @param processDefineId 流程定义Id
     * @param taskId          任务实例Id
     * @param choice          审批选择
     * @param choiceName      审批选择名称
     * @param params          业务数据keyvaluepair
     * @return
     */
    public FlowNodeDetail predictNextNode(String processDefineId, String taskId, Integer choice, String choiceName, Map<String, Object> params) {
        if (null == params) {
            params = new HashMap<>();
        }

        if (null != choice && null != choiceName) {
            params.put(CHOICE_VARIABLE_KEY, choice);
            params.put(CHOICENAME_VARIABLE_KEY, choiceName);
        }

        return processCoreService.predictNextNode(processDefineId, taskId, params);
    }

    /**
     * 待办已办优化接口
     *
     * @param userCode
     * @param pageIndex
     * @param pageSize
     * @param queryType
     * @param queryCondition
     * @return
     */
    public Page<TaskEntityWrapper> getMyProcessList(String userCode, int pageIndex, int pageSize, int queryType, Map queryCondition) {
        ManagementService managementService = processEngine.getManagementService();
        Page<TaskEntityWrapper> queryResult = managementService.executeCustomSql(new CustomTaskListExecution(userCode, pageIndex, pageSize, queryType, queryCondition));

        return queryResult;
    }

    /**
     * 获取流程明细
     *
     * @param processInstanceId 流程实例Id
     * @param taskId            任务实例Id
     * @param taskKey           任务定义Key
     * @param processDefineId   流程定义Id
     * @return
     * @throws Exception
     */
    public ProcessStepDetail getProcessStepDetail(String processInstanceId, String taskId, String taskKey, String processDefineId) throws Exception {
        ProcessStepDetail stepDetail = new ProcessStepDetail();
        ProcessDefinitionEntity definitionEntity = processCoreService.findProcessDefinitionEntityById(processDefineId);
        stepDetail.setProcessDefineId(definitionEntity.getId());
        stepDetail.setProcessDefineName(definitionEntity.getName());
        stepDetail.setProcessDefineKey(definitionEntity.getKey());
        ExtensionElementService extensionService = new ExtensionElementService(definitionEntity);
        ProcessTraceService traceService = new ProcessTraceService(processEngine);

        stepDetail.setTaskUIConfig(extensionService.getActivityRelateUIConfig(taskKey));
        stepDetail.setProcessInstanceId(processInstanceId);
        List<ProcessTraceInfo> traces = traceService.getProcessTraces(processInstanceId, stepDetail);
        stepDetail.setProcessTraces(traces);
        // 设置节点的类型，是否为会审节点
        ActivityImpl curActivity = definitionEntity.findActivity(taskKey);
        stepDetail.setJointNode(processCoreService.checkIsJointTask(curActivity));

        if (null != taskId) {
            HistoricTaskInstance task = processCoreService.findHistoricTaskInstanceByTaskIdAndInstanceId(taskId, processInstanceId);
            stepDetail.setTaskId(task.getId());
            stepDetail.setTaskName(task.getName());
            stepDetail.setTaskKey(task.getTaskDefinitionKey());
            stepDetail.setCreateTime(dateConverter.convert(String.class, task.getCreateTime()).toString());
            stepDetail.setHasHandle(task.getEndTime() != null);
            stepDetail.setChoices(extensionService.getActivityChoices(task.getTaskDefinitionKey()));
        } else {
            stepDetail.setHasHandle(true);
        }

        return stepDetail;
    }

    /**
     * 待办认领
     *
     * @param taskId   任务Id
     * @param userCode 认领人编码
     */
    public void claimPrcocessTask(String taskId, String userCode) {
        taskService.claim(taskId, userCode);
    }

    /**
     * 流程提交
     *
     * @param param
     * @throws Exception
     */
    public String submitProcessTask(Map<String, Object> param) throws Exception {
        // 解析流程相关入参
        Map processParam = MapUtils.getMap(param, ExtendFlowElementConstant.PROCESS_PARAM_OBJ_KEY, null);
        if (null == processParam) {
            throw new ActivitiException("流程入参对象__approvaldata不能为空!");
        }

        String jsonContent = JsonUtil.serialize(processParam);
        ProcessSubmitParam submitParam = JsonUtil.deserialize(jsonContent, ProcessSubmitParam.class);
        if (null == submitParam) {
            throw new ActivitiException("流程入参对象解析失败!");
        }

        if (StringUtils.isEmpty(submitParam.getBusinessObjName())) {
            throw new ActivitiException("获取业务对象名称失败!");
        }
        //Map<String, Object> bizData = MapUtils.getMap(param, submitParam.getBusinessObjName());

        param.remove(ExtendFlowElementConstant.PROCESS_PARAM_OBJ_KEY);
        submitParam.setBizData(param);
        submitProcessTask(submitParam);

        // 处理完成后返回流程实例Id
        return submitParam.getProcessInstanceId();
    }

    /**
     * 获取流程消息参数
     *
     * @param processInstanceId
     * @return
     */
    public FlowMessageParam getFlowMessageParam(String processInstanceId) {
        FlowMessageParam param = processCoreService.getCurrentTaskInfoByProcInstanceId(processInstanceId);
        if (param.isHasEnd()) {
            param.setForwardTaskState(new ArrayList<>());
            String sql = "select id_, task_def_key_, name_, assignee_ from act_hi_taskinst \n" +
                    "where proc_inst_id_ = #{processinstanceid} and assignee_ = #{applyusercode} order by end_time_ desc limit 1;\n";
            HistoricTaskInstance task = processEngine.getHistoryService().createNativeHistoricTaskInstanceQuery().sql(sql)
                    .parameter("processinstanceid", processInstanceId)
                    .parameter("applyusercode", param.getApplyUserCode()).singleResult();
            TaskBasic basic = new TaskBasic();
            basic.setTaskId(task.getId());
            basic.setTaskName(task.getName());
            basic.setTaskKey(task.getTaskDefinitionKey());
            basic.setHandler(task.getAssignee());

            param.getForwardTaskState().add(basic);
        }

        return param;
    }

    /**
     * 流程提交
     *
     * @param param
     * @throws Exception
     */
    protected void submitProcessTask(ProcessSubmitParam param) throws Exception {
        if (null == param) {
            throw new ActivitiIllegalArgumentException("提交参数不能为空");
        }

        // 处理审批选择和审批意见
        if (null != param.getChoice() && param.getChoiceName() != null) {
            taskService.setVariableLocal(param.getTaskId(), CHOICE_VARIABLE_KEY, param.getChoice());
            taskService.setVariableLocal(param.getTaskId(), CHOICENAME_VARIABLE_KEY, param.getChoiceName());
        }

        if (null != param.getComments()) {
            taskService.addComment(param.getTaskId(), param.getProcessInstanceId(), param.getComments());
        }

        if (null == param.getBizData()) {
            param.setBizData(new HashMap<String, Object>());
        }

        if (null != param.getChoice() && param.getChoiceName() != null) {
            param.getBizData().put(CHOICE_VARIABLE_KEY, param.getChoice());
            param.getBizData().put(CHOICENAME_VARIABLE_KEY, param.getChoiceName());
        }

        if (null == param.getChoice()) {
            // 发起人发起，默认无choice,走正常流转
            taskComplete(param);
        } else {
            switch (param.getChoice()) {
                case 3: // 中止
                    abandonProcess(param);
                    break;
                case 4: // 退回
                    fallBack(param);
                    break;
                default: // 正常流转
                    taskComplete(param);
                    break;
            }
        }
    }

    /**
     * 流程中止
     *
     * @param param
     * @throws Exception
     */
    protected void abandonProcess(ProcessSubmitParam param) throws Exception {
        // 获取当前节点的ActivityImpl
        ActivityImpl curActivity = processCoreService.findActivityImpl(param.getTaskId(), null);
        // 会签节点任务不能终止
        if (processCoreService.checkIsJointTask(curActivity)) {
            throw new ActivitiException("会签节点不能中止！");
        }

        // 获取结束节点ActiviyImpl对象
        ActivityImpl endActivity = processCoreService.findActivityImpl(param.getTaskId(), "__end");
        processCoreService.invokeAutoJumpTaskCmd(param.getTaskId(), curActivity, endActivity, param.getBizData(), "abandon");
    }

    /**
     * 流程退回
     *
     * @param param
     * @throws Exception
     */
    protected void fallBack(ProcessSubmitParam param) throws Exception {
        // 获取当前节点的ActivityImpl
        ActivityImpl curActivity = processCoreService.findActivityImpl(param.getTaskId(), null);
        // 会签节点任务不能退回
        if (processCoreService.checkIsJointTask(curActivity)) {
            throw new ActivitiException("会签节点不能做退回！");
        }

        if (StringUtils.isEmpty(param.getFallBackNodeKey())) {
            throw new ActivitiException("流程退回时退回节点不能为空!");
        }

        if (param.isNextStepIsMultiInstance()) {
            // 如果退回到的是会审节点，需设置会签迭代变量
            if (StringUtils.isEmpty(param.getNextStepUsers())) {
                logger.error("退回到的节点是会签节点，必须设置参与会签人员");
                throw new ActivitiException("下一步节点为会签节点，必须设置参与会签人员");
            }
            // 字符串分割
            List<String> nextUsers = Arrays.asList(param.getNextStepUsers().split("[\\s]*,[\\s]*"));
            param.getBizData().put("jointUsers", nextUsers);
        }

        ActivityImpl endActivity = processCoreService.findActivityImpl(param.getTaskId(), param.getFallBackNodeKey());
        processCoreService.invokeAutoJumpTaskCmd(param.getTaskId(), curActivity, endActivity, param.getBizData(), "fallback");

        if (!param.isNextStepIsMultiInstance() && StringUtils.isNotEmpty(param.getNextStepUsers())) {
            //对于退回到普通节点，需设置下一步的审批处理人
            Task task = taskService.createTaskQuery().processInstanceId(param.getProcessInstanceId()).active().singleResult();
            if (null != task && task.getAssignee() == null) {
                taskService.setAssignee(task.getId(), param.getNextStepUsers());
            }
        }
    }

    /**
     * 流程任务正常流转
     *
     * @param param
     * @throws Exception
     */
    protected void taskComplete(ProcessSubmitParam param) throws Exception {
        if (param.isNextStepIsMultiInstance()) {
            //如果下一步是会签节点,需设置会签迭代变量
            if (StringUtils.isEmpty(param.getNextStepUsers())) {
                logger.error("下一步节点为会签节点，必须设置参与会签人员");
                throw new ActivitiException("下一步节点为会签节点，必须设置参与会签人员");
            }
            // 字符串分割
            List<String> nextUsers = Arrays.asList(param.getNextStepUsers().split("[\\s]*,[\\s]*"));
            param.getBizData().put("jointUsers", nextUsers);
        }

        taskService.complete(param.getTaskId(), param.getBizData());

        if (!param.isCurrentStepIsMultiInstance() && !param.isNextStepIsMultiInstance() && StringUtils.isNotEmpty(param.getNextStepUsers())) {
            // 为普通节点设置下一步处理人
            Task task = taskService.createTaskQuery().processInstanceId(param.getProcessInstanceId()).active().singleResult();
            if (null != task && task.getAssignee() == null) {
                taskService.setAssignee(task.getId(), param.getNextStepUsers());
            }
        }
    }

    /**
     * 获取可以退回的节点列表
     *
     * @param taskKey           当前任务Key
     * @param processInstanceId 当前流程实例Id
     * @return
     * @throws Exception
     */
    public List<FallBackNode> getFallBackActivityList(String taskKey, String processInstanceId) throws Exception {
        List<FallBackNode> fallBackNodes = processCoreService.findBackActivity(taskKey, processInstanceId);
        return fallBackNodes;
    }

    /**
     * 获取退回节点的详情
     *
     * @param taskKey
     * @param processDefineId
     * @param processInstanceId
     * @param prevHandler
     * @return
     */
    public FlowNodeDetail getFallBackNodeDetail(String taskKey, String processDefineId, String processInstanceId, String prevHandler) {
        FlowNodeDetail nodeDetail = processCoreService.getFlowNodeDetail(taskKey, processDefineId, processInstanceId, prevHandler);
        return nodeDetail;
    }

    //endregion

    public FlowCategoryService getFlowCategoryService() {
        return flowCategoryService;
    }

    public FlowRelationService getFlowRelationService() {
        return flowRelationService;
    }

    public ProcessEngine getProcessEngine() {
        return this.processEngine;
    }
}
