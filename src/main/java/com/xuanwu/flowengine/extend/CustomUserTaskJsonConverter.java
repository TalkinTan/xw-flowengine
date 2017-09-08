package com.xuanwu.flowengine.extend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import com.xuanwu.flowengine.util.WorkflowUtils;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.UserTaskJsonConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 扩展Bpmn中UserTask的json与bpmnmodel之间的转换
 * File created on 2017/4/6.
 *
 * @author jkun
 */
public class CustomUserTaskJsonConverter extends UserTaskJsonConverter {
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        UserTask userTask = (UserTask) super.convertJsonToElement(elementNode, modelNode, shapeMap);
        JsonNode relateUIConfig = getProperty(ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE, elementNode);
        JsonNode userStategy = getProperty(ExtendFlowElementConstant.EXTEND_USER_STRATEGY_NAMESPACE, elementNode);
        String choices = getPropertyValueAsString(ExtendFlowElementConstant.EXTEND_CHOICE_NAMESPACE, elementNode);
        String mode = getPropertyValueAsString(ExtendFlowElementConstant.EXTEND_MODEL_NAMESPACE, elementNode);

        ExtensionElement extensionElement;
        ExtensionAttribute attribute;
        // 解析关联UI部分
        if (null != relateUIConfig && !relateUIConfig.isValueNode()) {
            extensionElement = new ExtensionElement();
            attribute = new ExtensionAttribute();

            extensionElement.setName(ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE);
            extensionElement.setNamespacePrefix("activiti");
            extensionElement.setNamespace("http://activiti.org/bpmn");
            attribute.setName(ExtendFlowElementConstant.EXTEND_UI_ELEMENT_KEY);
            attribute.setValue(relateUIConfig.toString());

            extensionElement.addAttribute(attribute);
            userTask.addExtensionElement(extensionElement);
        }

        // 解析人员策略
        if (null != userStategy) {
            extensionElement = new ExtensionElement();
            extensionElement.setName(ExtendFlowElementConstant.EXTEND_USER_STRATEGY_NAMESPACE);
            extensionElement.setNamespacePrefix("activiti");
            extensionElement.setNamespace("http://activiti.org/bpmn");
            JsonNode node;

            node = userStategy.get(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_ORGVALUE);
            if (null != node && !node.isNull()) {
                attribute = new ExtensionAttribute();
                attribute.setName(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_ORGVALUE);
                attribute.setValue(node.asText());
                extensionElement.addAttribute(attribute);
            }

            node = userStategy.get(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_TYPE);
            if (null != node && !node.isNull()) {
                attribute = new ExtensionAttribute();
                attribute.setName(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_TYPE);
                attribute.setValue(node.asText());
                extensionElement.addAttribute(attribute);
            }

            node = userStategy.get(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_POSTVALUE);
            if (null != node && !node.isNull()) {
                attribute = new ExtensionAttribute();
                attribute.setName(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_POSTVALUE);
                attribute.setValue(node.asText());
                extensionElement.addAttribute(attribute);
            }

            userTask.addExtensionElement(extensionElement);
        }

        // 解析审批选择
        if (StringUtils.isNotEmpty(choices)) {
            extensionElement = new ExtensionElement();
            attribute = new ExtensionAttribute();

            extensionElement.setName(ExtendFlowElementConstant.EXTEND_CHOICE_NAMESPACE);
            extensionElement.setNamespacePrefix("activiti");
            extensionElement.setNamespace("http://activiti.org/bpmn");
            attribute.setName(ExtendFlowElementConstant.EXTEND_CHOICE_KEY);
            attribute.setValue(choices);

            extensionElement.addAttribute(attribute);
            userTask.addExtensionElement(extensionElement);
        }

        // 解析选人模式
        if (StringUtils.isNotEmpty(mode)) {
            extensionElement = new ExtensionElement();
            attribute = new ExtensionAttribute();

            extensionElement.setName(ExtendFlowElementConstant.EXTEND_MODEL_NAMESPACE);
            extensionElement.setNamespacePrefix("activiti");
            extensionElement.setNamespace("http://activiti.org/bpmn");
            attribute.setName(ExtendFlowElementConstant.EXTEND_DEFAULT_KEY);
            attribute.setValue(mode);

            extensionElement.addAttribute(attribute);
            userTask.addExtensionElement(extensionElement);
        }


        return userTask;
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        WorkflowUtils.parseExtensionElementToJsonObjectField(baseElement, ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE,
                ExtendFlowElementConstant.EXTEND_UI_ELEMENT_KEY, propertiesNode);
        WorkflowUtils.parseExtensionElementToSingleJsonField(baseElement, ExtendFlowElementConstant.EXTEND_CHOICE_NAMESPACE,
                ExtendFlowElementConstant.EXTEND_CHOICE_KEY, propertiesNode);
        WorkflowUtils.parseExtensionElementToSingleJsonField(baseElement, ExtendFlowElementConstant.EXTEND_MODEL_NAMESPACE,
                ExtendFlowElementConstant.EXTEND_DEFAULT_KEY, propertiesNode);

        List<ExtensionElement> extensionElements = baseElement.getExtensionElements().get(ExtendFlowElementConstant.EXTEND_USER_STRATEGY_NAMESPACE);
        if (null != extensionElements && extensionElements.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode node = objectMapper.createObjectNode();
            String deptvalue = extensionElements.get(0).getAttributeValue(null, ExtendFlowElementConstant.EXTEND_USERSTRATEGY_ORGVALUE);
            String type = extensionElements.get(0).getAttributeValue(null, ExtendFlowElementConstant.EXTEND_USERSTRATEGY_TYPE);
            String rolevalue = extensionElements.get(0).getAttributeValue(null, ExtendFlowElementConstant.EXTEND_USERSTRATEGY_POSTVALUE);
            node.put(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_ORGVALUE, deptvalue);
            node.put(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_TYPE, type);
            node.put(ExtendFlowElementConstant.EXTEND_USERSTRATEGY_POSTVALUE, rolevalue);
            propertiesNode.set(ExtendFlowElementConstant.EXTEND_USER_STRATEGY_NAMESPACE, node);
        }
    }

}
