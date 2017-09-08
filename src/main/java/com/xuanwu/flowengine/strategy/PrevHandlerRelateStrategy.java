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
 * 上一步骤处理人运行时特殊策略解析类
 * File created on 2017/4/27.
 *
 * @author jkun
 */

public class PrevHandlerRelateStrategy extends BaseUserStrategy {

    /**
     * 部门类型
     */

    private SpecialStrategyType type;

    public PrevHandlerRelateStrategy(CustomUserEntityManager userEntityManager, SpecialStrategyType type, String prevHandlerId) {
        super(userEntityManager);
        this.type = type;
        this.prevHandlerId = prevHandlerId;
    }

    @Override
    public Collection<MemberBasicInfoDto> execute() {
        Set<MemberBasicInfoDto> users = new HashSet<>();

        if (StringUtils.isNotEmpty(prevHandlerId)) {
            List<MemberBasicInfoDto> deptUsers = getRelateUsersBySpecialStrategyType(prevHandlerId, type);
            if (null != deptUsers) {
                users.addAll(deptUsers);
            }
        }

        users = handleIntersactionOfPostStrategy(users);
        return users;
    }
}

