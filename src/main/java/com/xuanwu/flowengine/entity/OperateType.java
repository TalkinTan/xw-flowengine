package com.xuanwu.flowengine.entity;

/**
 * 自定义操作类型枚举 （新增、更新、删除、核查权限）
 * Created by jkun on 2017/3/18.
 *
 * @author jkun
 */
public enum OperateType {
    INSERT("新增"), UPDATE("更新"), DELETE("删除"), CHECKPERMISSION("核查权限");

    private String description;

    OperateType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
