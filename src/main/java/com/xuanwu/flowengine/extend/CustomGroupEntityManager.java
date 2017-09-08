package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.context.ProcessContext;
import com.xuanwu.flowengine.entity.CustomGroup;
import com.xuanwu.flowengine.entity.TenantIdentity;
import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.service.MemberQueryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 扩展Activiti内建的用户组管理器
 * Created by jkun on 2017/2/16.
 *
 * @author jkun
 */
public class CustomGroupEntityManager extends GroupEntityManager {

    private static final Logger logger = LoggerFactory.getLogger(CustomGroupEntityManager.class);

    /**
     * 成员、岗位、组织查询服务对象
     */
    private MemberQueryService memberQueryService;

    public CustomGroupEntityManager(MemberQueryService memberQueryService) {
        this.memberQueryService = memberQueryService;
    }

    public MemberQueryService getMemberQueryService() {
        return memberQueryService;
    }

    @Override
    public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();

            if (null != query.getId()) {
                Long id = Long.valueOf(query.getId());
                MemberBasicInfoDto postInfo = memberQueryService.queryPostInfoByPostId(identity.getTenantCode(), identity.getProductCode(), id);
                CustomGroup group = new CustomGroup(postInfo.getPostId().toString(), postInfo.getPostName());
                return Arrays.asList(group);
            }

            if (null != query.getUserId()) {
                Long id = Long.valueOf(query.getUserId());
                MemberBasicInfoDto memberInfo = memberQueryService.queryMemberInfoByMemberId(identity.getTenantCode(), identity.getProductCode(), id);
                if (null != memberInfo) {
                    CustomGroup group = new CustomGroup(memberInfo.getPostId().toString(), memberInfo.getPostName());
                    return Arrays.asList(group);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Group> findGroupsByUser(String userId) {

        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            Long id = Long.valueOf(userId);

            MemberBasicInfoDto memberInfo = memberQueryService.queryMemberInfoByMemberId(identity.getTenantCode(), identity.getProductCode(), id);
            if (null != memberInfo) {
                CustomGroup group = new CustomGroup(memberInfo.getPostId().toString(), memberInfo.getPostName());
                return Arrays.asList(group);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }
}
