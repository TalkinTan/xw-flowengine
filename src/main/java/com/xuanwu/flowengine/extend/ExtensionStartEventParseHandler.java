package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.StartEventParseHandler;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StartEvent自定义扩展解析
 * Created by jkun on 2017/3/9.
 *
 * @author jkun
 */
public class ExtensionStartEventParseHandler extends StartEventParseHandler {
    @Override
    protected void executeParse(BpmnParse bpmnParse, StartEvent startEvent) {
        super.executeParse(bpmnParse, startEvent);
        ActivityImpl activity = bpmnParse.getCurrentScope().findActivity(startEvent.getId());
        parseCustomUserTaskOperation(startEvent, activity, ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE);      //处理UI扩展元素
    }

    /**
     * 解析自定义的用户任务扩展，放置到ActivityImpl的Property中
     * @param startEvent
     * @param activity
     * @param extensionKey
     */
    protected void parseCustomUserTaskOperation(StartEvent startEvent, ActivityImpl activity, String extensionKey) {
        List<ExtensionElement> extensionElements = startEvent.getExtensionElements().get(extensionKey);
        if (null != extensionElements && extensionElements.size() > 0) {
            Map<String, String> map = new HashMap<>();
            for (ExtensionElement element : extensionElements) {
                if (null != element && !element.getAttributes().isEmpty()) {
                    for (List<ExtensionAttribute> attributes : element.getAttributes().values()) {
                        for (ExtensionAttribute attribute : attributes) {
                            map.put(attribute.getName(), attribute.getValue());
                        }
                    }
                }
            }

            if(!map.isEmpty()) {
                activity.setProperty(extensionKey, map);
            }
        }
    }
}
