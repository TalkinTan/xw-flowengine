package com.xuanwu.flowengine.strategy;

import com.xuanwu.flowengine.entity.SpecialStrategyType;
import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.extend.CustomUserEntityManager;
import com.xuanwu.flowengine.util.WorkflowUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 定义用户策略解析抽象类
 * File created on 2017/4/27.
 *
 * @author jkun
 */
public abstract class BaseUserStrategy {
    protected CustomUserEntityManager userEntityManager;
    protected String orgValues;            // 指定的组织
    protected String postValues;           // 指定的岗位
    protected String applyUserId;          // 发起人
    protected String prevHandlerId;        // 上一步骤处理人

    public BaseUserStrategy(CustomUserEntityManager userEntityManager) {
        this.userEntityManager = userEntityManager;
    }

    /**
     * 根据策略类型获取与传入成员相关的成员列表
     *
     * @param userCode
     * @param type
     * @return
     */
    public List<MemberBasicInfoDto> getRelateUsersBySpecialStrategyType(String userCode, SpecialStrategyType type) {
        List<MemberBasicInfoDto> result = null;
        switch (type) {
            case CURRENT_ORG_OF_POST:
                // 获取成员所在岗位的组织下的所有成员
                result = userEntityManager.queryOrgMemberListByMemberId(userCode);
                break;
            case PARENT_ORG_OF_POST:
                // 获取成员所在岗位的上级组织下的所有成员
                result = userEntityManager.queryParentOrgMemberListByMemberId(userCode);
                break;
            case PARENT_PARENT_ORG_OF_POST:
                // 获取成员所在岗位的上上级组织下的所有成员
                result = userEntityManager.queryPParentOrgMemberListByMemberId(userCode);
                break;
            case SAMELEVEL_ORG_OF_POST:
                // 获取成员所在岗位的同级组织下的所有成员
                result = userEntityManager.queryPeerOrgMemberListByMemberId(userCode);
                break;
            case PARENT_POST:
                // 获取成员所在岗位的上级岗位下的所有成员
                result = userEntityManager.queryParentPostMemberListByMemberId(userCode);
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * 处理并合并岗位策略，取交集
     *
     * @param originSet
     * @return
     */
    protected Set<MemberBasicInfoDto> handleIntersactionOfPostStrategy(Set<MemberBasicInfoDto> originSet) {
        if (StringUtils.isNotEmpty(postValues) && null != originSet) {
            List<String> posts = WorkflowUtils.extractString(postValues, ",");
            if (null != posts && posts.size() > 0) {
                List<MemberBasicInfoDto> postMemberList = userEntityManager.batchQueryMemberListByPosts(posts);
                if (originSet.isEmpty()) {
                    originSet.addAll(postMemberList);
                } else {
                    // 取交集
                    originSet = WorkflowUtils.handleIntersection(originSet, new HashSet<>(postMemberList));
                }
            }
        }

        return originSet;
    }

    public abstract Collection<MemberBasicInfoDto> execute();

    public String getOrgValues() {
        return orgValues;
    }

    public void setOrgValues(String orgValues) {
        this.orgValues = orgValues;
    }

    public String getPostValues() {
        return postValues;
    }

    public void setPostValues(String postValues) {
        this.postValues = postValues;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getPrevHandlerId() {
        return prevHandlerId;
    }

    public void setPrevHandlerId(String prevHandlerId) {
        this.prevHandlerId = prevHandlerId;
    }
}
