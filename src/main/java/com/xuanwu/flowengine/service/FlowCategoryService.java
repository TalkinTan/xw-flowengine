package com.xuanwu.flowengine.service;

import com.xuanwu.flowengine.entity.FlowCategoryEntity;
import com.xuanwu.flowengine.entity.Page;
import com.xuanwu.flowengine.entity.dto.FlowCategoryDetailDto;

import java.util.List;
import java.util.Map;

/**
 * 流程种类服务接口类
 * Created by jkun on 2017/3/15.
 *
 * @author jkun
 */
public interface FlowCategoryService {
    /**
     * 新增
     *
     * @param entity
     */
    boolean insertFlowCategory(FlowCategoryEntity entity);

    /**
     * 修改
     *
     * @param entity
     */
    boolean updateFlowCategory(FlowCategoryEntity entity);

    /**
     * 删除
     *
     * @param flowCategoryCode
     */
    boolean deleteFlowCategory(Long flowCategoryCode);

    /**
     * 查询流程分类列表
     *
     * @return
     */
    List<FlowCategoryEntity> selectAll();

    /**
     * 根据编码获取流程分类信息
     *
     * @param flowCategoryCode
     * @return
     */
    FlowCategoryEntity getFlowCategoryByKeys(Long flowCategoryCode);

    /**
     * 获取流程分类明细列表
     *
     * @return
     */
    List<FlowCategoryDetailDto> getFlowCategoryDetail();

    /**
     * 带权限查询流程分类明细列表
     *
     * @param positionId
     * @return
     */
    List<FlowCategoryDetailDto> getFlowCategoryDetailWithPermission(Long positionId);

    /**
     * 权限检查，根据流程Key检查发起权限
     *
     * @param positionId
     * @param processKey
     * @return
     */
    boolean checkPermissionByProcessKey(Long positionId, String processKey);

    /**
     * 获取流程分类下挂载的流程分页列表
     *
     * @param flowCategoryCode
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<Map> getProcessPageListByCategoryCode(Long flowCategoryCode, int pageIndex, int pageSize);
}
