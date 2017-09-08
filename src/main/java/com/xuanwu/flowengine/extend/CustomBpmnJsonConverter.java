package com.xuanwu.flowengine.extend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import com.xuanwu.flowengine.util.WorkflowUtils;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverterUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 扩展Bpmn Json解析类
 * File created on 2017/4/6.
 *
 * @author jkun
 */
public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    public void changeDefaultConverter() {
        convertersToBpmnMap.put(STENCIL_EVENT_START_NONE, CustomStartEventJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_EVENT_START_TIMER, CustomStartEventJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_EVENT_START_ERROR, CustomStartEventJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_EVENT_START_MESSAGE, CustomStartEventJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_EVENT_START_SIGNAL, CustomStartEventJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomUserTaskJsonConverter.class);

        convertersToJsonMap.put(StartEvent.class, CustomStartEventJsonConverter.class);
        convertersToJsonMap.put(UserTask.class, CustomUserTaskJsonConverter.class);
    }

    @Override
    public BpmnModel convertToBpmnModel(JsonNode modelNode) {
        BpmnModel bpmnModel = super.convertToBpmnModel(modelNode);
        Process process = bpmnModel.getMainProcess();
        if (null != process) {
            String instanceRule = BpmnJsonConverterUtil.getPropertyValueAsString(ExtendFlowElementConstant.EXTEND_PROCESSINSTANCERULE_NAMESPACE, modelNode);
            if (!StringUtils.isEmpty(instanceRule)) {
                // 处理流程配置properties里面的流程实例名生成规则
                ExtensionElement element = new ExtensionElement();
                ExtensionAttribute attribute = new ExtensionAttribute();

                element.setName(ExtendFlowElementConstant.EXTEND_PROCESSINSTANCERULE_NAMESPACE);
                element.setNamespacePrefix("activiti");
                element.setNamespace("http://activiti.org/bpmn");
                attribute.setName(ExtendFlowElementConstant.EXTEND_DEFAULT_KEY);
                attribute.setValue(instanceRule);
                element.addAttribute(attribute);
                process.addExtensionElement(element);
            }
        }

        return bpmnModel;
    }

    @Override
    public ObjectNode convertToJson(BpmnModel model) {
        ObjectNode modelNode = super.convertToJson(model);
        ObjectNode mainPropertyNode = (ObjectNode) modelNode.get(EDITOR_SHAPE_PROPERTIES);
        // 将流程的实例名生成规则配置写到流程配置json的最外层properties属性里
        Process process = model.getMainProcess();
        WorkflowUtils.parseExtensionElementToSingleJsonField(process, ExtendFlowElementConstant.EXTEND_PROCESSINSTANCERULE_NAMESPACE,
                ExtendFlowElementConstant.EXTEND_DEFAULT_KEY, mainPropertyNode);
        return modelNode;
    }
}
