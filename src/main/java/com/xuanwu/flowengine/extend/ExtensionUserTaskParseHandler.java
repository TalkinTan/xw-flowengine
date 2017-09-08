package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义的UserTask属性解析类
 * Created by jkun on 2017/2/8.
 */
public class ExtensionUserTaskParseHandler extends UserTaskParseHandler {
    @Override
    protected void executeParse(BpmnParse bpmnParse, UserTask userTask) {
        super.executeParse(bpmnParse, userTask);
        ActivityImpl activity = bpmnParse.getCurrentScope().findActivity(userTask.getId());
        parseCustomUserTaskOperation(userTask, activity, ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE);      //处理UI扩展元素
        parseCustomUserTaskOperation(userTask, activity, ExtendFlowElementConstant.EXTEND_USER_STRATEGY_NAMESPACE);   //处理用户策略扩展元素
        parseCustomUserTaskOperation(userTask, activity, ExtendFlowElementConstant.EXTEND_CHOICE_NAMESPACE);          //处理用户审批选择项
        parseCustomUserTaskOperation(userTask, activity, ExtendFlowElementConstant.EXTEND_MODEL_NAMESPACE);           //处理用户任务模式配置
    }

    /**
     * 解析自定义的用户任务扩展，放置到ActivityImpl的Property中
     *
     * @param userTask
     * @param activity
     * @param extensionKey
     */
    protected void parseCustomUserTaskOperation(UserTask userTask, ActivityImpl activity, String extensionKey) {
        List<ExtensionElement> extensionElements = userTask.getExtensionElements().get(extensionKey);
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

            if (!map.isEmpty()) {
                activity.setProperty(extensionKey, map);
            }
        }
    }
}
