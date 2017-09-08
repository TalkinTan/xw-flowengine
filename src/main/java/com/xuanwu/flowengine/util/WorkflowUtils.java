package com.xuanwu.flowengine.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.ExtensionElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by jkun on 2017/3/6.
 *
 * @author jkun
 */
public class WorkflowUtils {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowUtils.class);
    private static final String[] BPMN_RESOURCE_SUFFIXES = new String[]{".bpmn20.xml", ".bpmn"};  //流程名允许的后缀名
    private static final Map<String, String> typeNameMapping = new HashMap<>();

    static {
        typeNameMapping.put("userTask", "用户任务");
        typeNameMapping.put("serviceTask", "系统任务");
        typeNameMapping.put("startEvent", "开始节点");
        typeNameMapping.put("endEvent", "结束节点");
        typeNameMapping.put("exclusiveGateway", "条件网关(系统自动根据条件处理)");
        typeNameMapping.put("inclusiveGateway", "并行网关");
        typeNameMapping.put("callActivity", "子流程");
    }

    /**
     * 转换流程节点类型为中文名
     *
     * @param type
     * @return
     */
    public static String parseTypeName(String type) {
        return typeNameMapping.get(type) == null ? type : typeNameMapping.get(type);
    }

    /**
     * 处理流程名
     *
     * @param name
     * @return
     */
    public static String handleProcessName(String name) {
        boolean checkSuffix = false;
        for (String suffix : BPMN_RESOURCE_SUFFIXES) {
            if (name.endsWith(suffix)) {
                checkSuffix = true;
                break;
            }
        }

        if (!checkSuffix) {
            name = name.concat(BPMN_RESOURCE_SUFFIXES[0]);
        }

        return name;
    }

    /**
     * 将扩展元素解析成普通字符串设置到指定ObjectNode的属性下
     *
     * @param baseElement
     * @param namespace
     * @param key
     * @param propertiesNode
     */
    public static void parseExtensionElementToSingleJsonField(BaseElement baseElement, String namespace, String key, ObjectNode propertiesNode) {
        List<ExtensionElement> extensionElements = baseElement.getExtensionElements().get(namespace);
        if (null != extensionElements && extensionElements.size() > 0) {
            String value = extensionElements.get(0).getAttributeValue(null, key);
            if (StringUtils.isNotEmpty(value)) {
                propertiesNode.put(namespace, value);
            }
        }
    }

    /**
     * 将扩展元素的值解析成jsonNode设置到指定的ObjectNode的属性下
     *
     * @param baseElement
     * @param namespace
     * @param key
     * @param propertiesNode
     */
    public static void parseExtensionElementToJsonObjectField(BaseElement baseElement, String namespace, String key, ObjectNode propertiesNode) {
        List<ExtensionElement> extensionElements = baseElement.getExtensionElements().get(namespace);
        if (null != extensionElements && extensionElements.size() > 0) {
            String value = extensionElements.get(0).getAttributeValue(null, key);
            if (StringUtils.isNotEmpty(value)) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode node = mapper.readTree(value);
                    propertiesNode.set(namespace, node);
                } catch (IOException e) {
                    logger.error("转换relateui的配置json时出错");
                }
            }
        }
    }

    /**
     * 取交集，需重写具体类型的equals和hashCode方法
     *
     * @param leftSet
     * @param rightSet
     * @return
     */
    public static <T> Set<T> handleIntersection(Set<T> leftSet, Set<T> rightSet) {
        Set<T> result = new HashSet<>();
        for (T item : leftSet) {
            if (rightSet.contains(item)) {
                result.add(item);
            }
        }

        return result;
    }

    /**
     * 从租户线程变量值里获取租户编码和产品编码信息
     *
     * @param tenantCombineKey
     * @return
     */
    public static Long[] extractTenantInfo(String tenantCombineKey) {
        if (StringUtils.isNotEmpty(tenantCombineKey)) {
            String[] strArrays = StringUtils.split(tenantCombineKey, "|");
            return new Long[]{Long.valueOf(strArrays[0]), Long.valueOf(strArrays[1]), Long.valueOf(strArrays[2]), Long.valueOf(strArrays[3])};
        }

        return null;
    }

    /**
     * 分隔符分割字符串
     *
     * @param str
     * @return
     */
    public static List<String> extractString(String str, String delimeter) {
        String pattern = String.format("[\\s]*%s[\\s]*", delimeter);
        return Arrays.asList(str.split(pattern));
    }

}
