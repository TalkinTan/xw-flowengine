package com.xuanwu.flowengine.strategy;

import com.xuanwu.flowengine.entity.SpecialStrategyType;
import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.extend.CustomUserEntityManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 发起人运行时相关特殊策略解析类
 * File created on 2017/4/27.
 *
 * @author jkun
 */
public class ApplyRelateStrategy extends BaseUserStrategy {

    /**
     * 部门类型
     */
    private SpecialStrategyType type;

    public ApplyRelateStrategy(CustomUserEntityManager userEntityManager, SpecialStrategyType type, String applyUserId) {
        super(userEntityManager);
        this.type = type;
        this.applyUserId = applyUserId;
    }

    @Override
    public Collection<MemberBasicInfoDto> execute() {
        Set<MemberBasicInfoDto> users = new HashSet<>();

        if (StringUtils.isNotEmpty(applyUserId)) {
            List<MemberBasicInfoDto> deptUsers = getRelateUsersBySpecialStrategyType(applyUserId, type);
            if (null != deptUsers) {
                users.addAll(deptUsers);
            }
        }

        users = handleIntersactionOfPostStrategy(users);
        return users;
    }
}
