package com.xuanwu.flowengine.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义流程元素常量
 * Created by jkun on 2017/3/9.
 *
 * @author jkun
 */
public class ExtendFlowElementConstant {

    public static final String EXTEND_USER_STRATEGY_NAMESPACE = "userstrategy";     //处理自定义用户策略扩展元素

    public static final String EXTEND_USERSTRATEGY_ORGVALUE = "orgvalue";           //组织策略Key

    public static final String EXTEND_USERSTRATEGY_POSTVALUE = "postvalue";         //岗位策略Key

    public static final String EXTEND_USERSTRATEGY_TYPE = "type";                   // 自定义用户策略类型

    public static final String EXTEND_UI_ELEMENT_NAMESPACE = "relateui";            //UI扩展元素

    public static final String EXTEND_UI_ELEMENT_KEY = "uiconfig";                    //UI编码Key

    public static final String EXTEND_CHOICE_NAMESPACE = "choice";                  //处理用户审批选择项

    public static final String EXTEND_CHOICE_KEY = "value";                         //用户审批选择项Key

    public static final String EXTEND_MODEL_NAMESPACE = "mode";                     //用户任务模式设置（认领|选人）

    public static final String EXTEND_DEFAULT_KEY = "value";                        //默认扩展Key

    public static final String EXTEND_FIRSTSTEP_ID = "af_initiatetask";             //约定流程第一步节点的Id

    public static final String EXTEND_PROCESSINSTANCERULE_NAMESPACE = "processinstancerule"; //流程实例名生成规则

    public static final Map<String, SpecialStrategyType> userStrategyTypeMapper = new HashMap<>(); // 部门策略类型枚举映射

    public static final String[] applyRelateType = new String[]{"orgofapplypost", "peerorgofapplypost", "parentorgofapplypost", "pparentorgofapplypost"};  // 运行时特殊策略与发起人相关的类型集合

    public static final String[] prevStepHandlerRelateType = new String[]{"orgofprevhandlerpost", "peerorgofprevhandlerpost", "parentorgofprevhandlerpost", "prevhandlerparentpost"}; //运行时特殊策略与上一步骤处理人相关的类型集合

    public static final String fixedOrgTypeName = "fixorg";  // 指定组织策略类型

    public static final String fixedPostTypeName = "fixpost"; // 指定岗位策略类型

    public static final String PROCESS_PARAM_OBJ_KEY = "__approvaldata"; // 客户端传递的流程入参对象Key

    static {
        userStrategyTypeMapper.put("orgofapplypost", SpecialStrategyType.CURRENT_ORG_OF_POST);
        userStrategyTypeMapper.put("peerorgofapplypost", SpecialStrategyType.SAMELEVEL_ORG_OF_POST);
        userStrategyTypeMapper.put("parentorgofapplypost", SpecialStrategyType.PARENT_ORG_OF_POST);
        userStrategyTypeMapper.put("pparentorgofapplypost", SpecialStrategyType.PARENT_PARENT_ORG_OF_POST);
        userStrategyTypeMapper.put("orgofprevhandlerpost", SpecialStrategyType.CURRENT_ORG_OF_POST);
        userStrategyTypeMapper.put("peerorgofprevhandlerpost", SpecialStrategyType.SAMELEVEL_ORG_OF_POST);
        userStrategyTypeMapper.put("parentorgofprevhandlerpost", SpecialStrategyType.PARENT_ORG_OF_POST);
        userStrategyTypeMapper.put("prevhandlerparentpost", SpecialStrategyType.PARENT_POST);
    }
}
