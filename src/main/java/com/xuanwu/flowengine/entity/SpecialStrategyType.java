package com.xuanwu.flowengine.entity;

/**
 * 特殊人员策略，运行时相关（与发起人和上一步骤处理人相关）
 * File created on 2017/4/28.
 *
 * @author jkun
 */
public enum SpecialStrategyType {
    CURRENT_ORG_OF_POST("所在岗位的组织"), PARENT_ORG_OF_POST("所在岗位的上级组织"),
    PARENT_PARENT_ORG_OF_POST("所在岗位的上上级组织"), SAMELEVEL_ORG_OF_POST("所在岗位的同级组织"),
    PARENT_POST("所在岗位的上级岗位");

    private String description;

    SpecialStrategyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
