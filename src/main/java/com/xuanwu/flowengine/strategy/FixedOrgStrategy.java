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
 * 固定组织策略解析
 * File created on 2017/4/27.
 *
 * @author jkun
 */
public class FixedOrgStrategy extends BaseUserStrategy {

    public FixedOrgStrategy(CustomUserEntityManager userEntityManager, String orgValues) {
        super(userEntityManager);
        this.orgValues = orgValues;
    }

    @Override
    public Collection<MemberBasicInfoDto> execute() {
        Set<MemberBasicInfoDto> users = new HashSet<>();

        if (StringUtils.isNotEmpty(orgValues)) {
            List<String> orgs = WorkflowUtils.extractString(orgValues, ",");
            if (null != orgs && orgs.size() > 0) {
                List<MemberBasicInfoDto> orgMemberList = userEntityManager.batchQueryMemberListByOrgs(orgs);
                if (null != orgMemberList) {
                    users.addAll(orgMemberList);
                }
            }
        }

        users = handleIntersactionOfPostStrategy(users);
        return users;
    }
}
