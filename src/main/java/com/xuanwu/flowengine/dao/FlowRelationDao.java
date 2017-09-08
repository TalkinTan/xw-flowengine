package com.xuanwu.flowengine.dao;

import com.xuanwu.flowengine.entity.FlowCategoryRelationEntity;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.dto.FlowCategoryRelationListDto;

/**
 * 流程关联关系Dao层接口类
 * Created by jkun on 2017/3/17.
 *
 * @author jkun
 */
public interface FlowRelationDao {

    /**
     * 新增关联关系
     *
     * @param entity
     */
    void insertFlowRelation(FlowCategoryRelationEntity entity);

    /**
     * 修改关联关系
     *
     * @param entity
     */
    void updateFlowRelation(FlowCategoryRelationEntity entity);

    /**
     * 删除关联关系
     *
     * @param flowDefineCode
     */
    void deleteFlowRelation(Long flowDefineCode);

    /**
     * 分页获取关联关系列表
     *
     * @param directoryCode
     * @param flowModelName
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<FlowCategoryRelationListDto> getFlowListPage(Long directoryCode, String flowModelName, int pageIndex, int pageSize);
}
