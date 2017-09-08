package com.xuanwu.flowengine.entity;

/**
 * 流程状态枚举
 * Created by jkun on 2017/2/23.
 *
 * @author jkun
 */
public enum ProcessStatus {
    HANDLING("处理中"), DRAFT("草稿"),
    FALLBACK("退回"), HAS_END("已结束"), ABANDON("已终止");

    private String statusName;

    public String getStatusName() {
        return this.statusName;
    }

    ProcessStatus(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return this.statusName;
    }
}
