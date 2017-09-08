package com.xuanwu.flowengine.extend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import com.xuanwu.flowengine.util.WorkflowUtils;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.StartEventJsonConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 扩展Bpmn中StartEvent的json与bpmnmodel之间的转换
 * File created on 2017/4/6.
 *
 * @author jkun
 */
public class CustomStartEventJsonConverter extends StartEventJsonConverter {
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        StartEvent startEvent = (StartEvent) super.convertJsonToElement(elementNode, modelNode, shapeMap);
        String relateUICode = getPropertyValueAsString(ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE, elementNode);
        if (StringUtils.isNotEmpty(relateUICode)) {
            // 处理JsonNode中StartEvent里的ui扩展内容
            ExtensionElement element = new ExtensionElement();
            ExtensionAttribute attribute = new ExtensionAttribute();

            element.setName(ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE);
            element.setNamespacePrefix("activiti");
            element.setNamespace("http://activiti.org/bpmn");
            attribute.setName(ExtendFlowElementConstant.EXTEND_UI_ELEMENT_KEY);
            attribute.setValue(relateUICode);

            element.addAttribute(attribute);
            startEvent.addExtensionElement(element);
        }

        return startEvent;
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        WorkflowUtils.parseExtensionElementToSingleJsonField(baseElement, ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE,
                ExtendFlowElementConstant.EXTEND_UI_ELEMENT_KEY, propertiesNode);
    }

}
