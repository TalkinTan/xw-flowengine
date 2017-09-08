package com.xuanwu.flowengine.entity;

import java.io.Serializable;

/**
 * 租户身份标识对象定义
 * File created on 2017/5/25.
 *
 * @author jkun
 */
public class TenantIdentity implements Serializable {
    private static final long serialVersionUID = 1l;

    private Long tenantCode;

    private Long productCode;

    private Long workId;

    private Long dataCenterId;

    public TenantIdentity() {
    }

    public TenantIdentity(Long tenantCode, Long productCode, Long workId, Long dataCenterId) {
        this.tenantCode = tenantCode;
        this.productCode = productCode;
        this.workId = workId;
        this.dataCenterId = dataCenterId;
    }

    public Long getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(Long tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Long getProductCode() {
        return productCode;
    }

    public void setProductCode(Long productCode) {
        this.productCode = productCode;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(Long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }
}
