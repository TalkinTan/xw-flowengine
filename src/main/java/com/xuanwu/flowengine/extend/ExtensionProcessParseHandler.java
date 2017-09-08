package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.ProcessParseHandler;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

import java.util.List;

/**
 * 自定义的Process附加属性解析器
 * File created on 2017/5/22.
 *
 * @author jkun
 */
public class ExtensionProcessParseHandler extends ProcessParseHandler {
    @Override
    protected ProcessDefinitionEntity transformProcess(BpmnParse bpmnParse, Process process) {
        ProcessDefinitionEntity definitionEntity = super.transformProcess(bpmnParse, process);
        List<ExtensionElement> extensionElements = process.getExtensionElements().get(ExtendFlowElementConstant.EXTEND_PROCESSINSTANCERULE_NAMESPACE);
        if (null != extensionElements && extensionElements.size() > 0) {
            // 解析流程实例名生成规则
            String processInstanceRule = extensionElements.get(0).getAttributeValue(null, ExtendFlowElementConstant.EXTEND_DEFAULT_KEY);
            definitionEntity.setProperty(ExtendFlowElementConstant.EXTEND_PROCESSINSTANCERULE_NAMESPACE, processInstanceRule);
        }

        return definitionEntity;
    }
}
