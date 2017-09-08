package com.xuanwu.flowengine.mapper;

import com.xuanwu.flowengine.entity.FlowCategoryEntity;
import com.xuanwu.flowengine.entity.dto.FlowCategoryDetailDto;

import java.util.List;
import java.util.Map;

/**
 * 流程分类Mapper接口
 * File created on 2017/6/17.
 *
 * @author jkun
 */
public interface FlowCategoryMapper {

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(FlowCategoryEntity entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean update(FlowCategoryEntity entity);

    /**
     * 删除
     *
     * @param flowCategoryCode
     * @return
     */
    boolean delete(Long flowCategoryCode);

    /**
     * 查询流程分类列表
     *
     * @return
     */
    List<FlowCategoryEntity> selectAll();

    /**
     * 根据分类编码查询
     *
     * @param flowCategoryCode
     * @return
     */
    FlowCategoryEntity selectById(Long flowCategoryCode);

    /**
     * 查询流程分类明细，供移动端使用, 不带权限控制，630以后需使用带权限的方法
     *
     * @return
     */
    List<FlowCategoryDetailDto> selectDetailWithoutPrivilege();

    /**
     * 查询流程分类明细，供移动端使用, 带权限控制
     *
     * @param positionId
     * @return
     */
    List<FlowCategoryDetailDto> selectCategoryDetailWithPrivilege(Long positionId);

    /**
     * 权限检查，根据流程Key
     *
     * @param map
     * @return
     */
    long checkPrivilegeByProcessKey(Map map);

    /**
     * 流程分类名的查重
     *
     * @param map
     * @return
     */
    long checkIfExists(Map map);

    /**
     * 根据流程分类获取分类下挂载的流程数量
     *
     * @param flowCategoryCode
     * @return
     */
    long getProcessCountByCategoryCode(Long flowCategoryCode);

    /**
     * 根据流程分类获取分类下挂载的流程列表
     *
     * @param map
     * @return
     */
    List<Map> getProcessListByCategoryCode(Map map);
}
