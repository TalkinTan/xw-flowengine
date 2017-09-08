package com.xuanwu.flowengine.entity.dto;

import com.xuanwu.flowengine.entity.FlowCategoryEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程分类树结构DTO
 * File created on 2017/6/19.
 *
 * @author jkun
 */
public class FlowCategoryTreeDto extends FlowCategoryEntity {
    /**
     * 子节点
     */
    private List<FlowCategoryTreeDto> children;

    public List<FlowCategoryTreeDto> getChildren() {
        return children;
    }

    public void setChildren(List<FlowCategoryTreeDto> children) {
        this.children = children;
    }

    /**
     * build流程分类目录树
     *
     * @param list
     * @return
     */
    public static List<FlowCategoryTreeDto> buildTreeStruct(List<FlowCategoryEntity> list) {
        if (null == list || list.size() == 0) {
            return null;
        }

        List<FlowCategoryTreeDto> result = new ArrayList<>();
        // 查找顶层节点
        for (FlowCategoryEntity item : list) {
            long size = list.stream().filter(i -> i.getFlowCategoryCode().equals(item.getParentCode())).count();
            if (size == 0) {
                FlowCategoryTreeDto treeDto = new FlowCategoryTreeDto();
                treeDto.setFlowCategoryCode(item.getFlowCategoryCode());
                treeDto.setFlowCategoryName(item.getFlowCategoryName());
                treeDto.setParentCode(item.getParentCode());
                treeDto.setStatus(item.getStatus());
                result.add(treeDto);
            }
        }

        result.forEach(i -> recursiveNodeLevel(i, list));
        return result;
    }

    /**
     * 递归处理节点树
     *
     * @param original
     * @param list
     */
    private static void recursiveNodeLevel(FlowCategoryTreeDto original, List<FlowCategoryEntity> list) {
        for (FlowCategoryEntity item : list) {
            if (item.getParentCode() != null && item.getParentCode().equals(original.getFlowCategoryCode())) {
                if (original.getChildren() == null) {
                    original.setChildren(new ArrayList<>());
                }

                FlowCategoryTreeDto treeDto = new FlowCategoryTreeDto();
                treeDto.setFlowCategoryCode(item.getFlowCategoryCode());
                treeDto.setFlowCategoryName(item.getFlowCategoryName());
                treeDto.setParentCode(item.getParentCode());
                treeDto.setStatus(item.getStatus());

                original.getChildren().add(treeDto);
                recursiveNodeLevel(treeDto, list);
            }
        }
    }
}
