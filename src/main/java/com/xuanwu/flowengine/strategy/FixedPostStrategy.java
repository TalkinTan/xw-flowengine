package com.xuanwu.flowengine.strategy;

import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.extend.CustomUserEntityManager;
import com.xuanwu.flowengine.util.WorkflowUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 固定岗位策略解析
 * File created on 2017/5/22.
 *
 * @author jkun
 */
public class FixedPostStrategy extends BaseUserStrategy {

    public FixedPostStrategy(CustomUserEntityManager userEntityManager, String postValues) {
        super(userEntityManager);
        this.postValues = postValues;
    }

    @Override
    public Collection<MemberBasicInfoDto> execute() {
        Set<MemberBasicInfoDto> users = new HashSet<>();
        if (StringUtils.isNotEmpty(postValues)) {
            List<String> posts = WorkflowUtils.extractString(postValues, ",");
            if (null != posts && posts.size() > 0) {
                List<MemberBasicInfoDto> postMemberList = userEntityManager.batchQueryMemberListByPosts(posts);
                if (null != postMemberList) {
                    users.addAll(postMemberList);
                }
            }
        }

        return users;
    }
}
