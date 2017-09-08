package com.xuanwu.flowengine.service;

import com.xuanwu.flowengine.entity.FlowCategoryRelationEntity;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.dto.FlowCategoryRelationListDto;

/**
 * 流程关联关系服务接口类
 * Created by jkun on 2017/3/17.
 *
 * @author jkun
 */
public interface FlowRelationService {
    /**
     * 查询流程（模型）列表
     *
     * @param diretoryCode
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<FlowCategoryRelationListDto> getFlowRelationListPage(long diretoryCode, String flowModelName, int pageIndex, int pageSize);

    /**
     * 保存流程（模型）与目录和分类的关联关系
     *
     * @param entity
     * @return
     */
    boolean saveFlowRelation(FlowCategoryRelationEntity entity);

    /**
     * 根据关联关系Id移除关联关系
     *
     * @param flowRelationCode
     * @return
     */
    boolean deleteFlowRelationByCode(Long flowRelationCode);
}
