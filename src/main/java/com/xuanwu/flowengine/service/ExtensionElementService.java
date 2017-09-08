package com.xuanwu.flowengine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuanwu.flowengine.entity.ExtendFlowElementConstant;
import com.xuanwu.flowengine.entity.ProcessChoice;
import org.activiti.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扩展元素服务
 * Created by jkun on 2017/3/10.
 *
 * @author jkun
 */
public class ExtensionElementService {
    private static final Logger log = LoggerFactory.getLogger(ExtensionElementService.class);

    private ProcessDefinitionEntity definitionEntity;

    public ExtensionElementService(ProcessDefinitionEntity definitionEntity) {
        this.definitionEntity = definitionEntity;
    }

    /**
     * 获取流程实例名的生成规则
     *
     * @return
     */
    public String getProcessInstanceNameRule() {
        if (null != definitionEntity) {
            Object nameRule = definitionEntity.getProperty(ExtendFlowElementConstant.EXTEND_PROCESSINSTANCERULE_NAMESPACE);
            if (null != nameRule && nameRule instanceof String) {
                return nameRule.toString();
            }
        }

        return null;
    }

    /**
     * 获取发起节点关联的UI编码
     *
     * @return
     */
    @Deprecated
    public JsonNode getApplyActivityRelateUIConfig() throws Exception {
        if (null != definitionEntity) {
            List<ActivityImpl> activities = definitionEntity.getActivities();
            for (ActivityImpl activity : activities) {
                if (activity.getActivityBehavior() instanceof NoneStartEventActivityBehavior) {
                    return getActivityRelateUIConfig(activity.getId());
                }
            }
        }

        return null;
    }

    /**
     * 获取节点关联的UI配置
     *
     * @param actId
     * @return
     */
    public JsonNode getActivityRelateUIConfig(String actId) throws Exception {
        log.debug("start get ui config of activity, activityId: " + actId);

        if (null != definitionEntity && StringUtils.isNotEmpty(actId)) {
            ActivityImpl activity = definitionEntity.findActivity(actId);
            if (null != activity) {
                Object obj = activity.getProperty(ExtendFlowElementConstant.EXTEND_UI_ELEMENT_NAMESPACE);
                if (null != obj && obj instanceof HashMap) {
                    Map map = (HashMap) obj;
                    Object value = map.get(ExtendFlowElementConstant.EXTEND_UI_ELEMENT_KEY);
                    if (null != value && value instanceof String) {
                        log.debug("the ui config of activity which id is " + actId + " is " + value.toString());
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode uiConfig = mapper.readTree(value.toString());
                        return uiConfig;
                    }
                }
            } else {
                log.error("can not find the activity which id is %s", actId);
            }
        }

        log.debug("this activity which id is " + actId + "do not have ui config");
        return null;
    }

    /**
     * 获取任务节点的审批选择范围
     *
     * @param actId
     * @return
     */
    public Collection<ProcessChoice> getActivityChoices(String actId) {
        String choiceConfig = StringUtils.EMPTY;
        if (null != definitionEntity && StringUtils.isNotEmpty(actId)) {
            ActivityImpl activity = definitionEntity.findActivity(actId);
            if (null != activity) {
                Object obj = activity.getProperty(ExtendFlowElementConstant.EXTEND_CHOICE_NAMESPACE);
                if (null != obj && obj instanceof HashMap) {
                    Map map = (HashMap) obj;
                    Object value = map.get(ExtendFlowElementConstant.EXTEND_CHOICE_KEY);
                    if (null != value && value instanceof String && StringUtils.isNotEmpty(value.toString())) {
                        log.debug("the choice range of activity which id is %s is %s", actId, value.toString());
                        choiceConfig = value.toString();
                    }
                }
            } else {
                log.error("can not find the activity which id is %s", actId);
                return null;
            }
        }

        return ProcessChoice.splitChoicesFromString(choiceConfig);
    }
}
