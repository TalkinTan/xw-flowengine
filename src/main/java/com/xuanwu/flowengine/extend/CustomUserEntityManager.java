package com.xuanwu.flowengine.extend;

import com.xuanwu.flowengine.context.ProcessContext;
import com.xuanwu.flowengine.entity.CustomGroup;
import com.xuanwu.flowengine.entity.CustomUser;
import com.xuanwu.flowengine.entity.TenantIdentity;
import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.service.MemberQueryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 扩展Activiti内建的用户管理器
 * Created by jkun on 2017/2/15.
 *
 * @author jkun
 */
public class CustomUserEntityManager extends UserEntityManager {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserEntityManager.class);

    /**
     * 成员、岗位、组织查询服务对象
     */
    private MemberQueryService memberQueryService;

    public CustomUserEntityManager(MemberQueryService memberQueryService) {
        this.memberQueryService = memberQueryService;
    }

    public MemberQueryService getMemberQueryService() {
        return memberQueryService;
    }

    @Override
    public User findUserById(String userId) {
        MemberBasicInfoDto memberInfo = findMemberInfoByMemberId(userId);
        if (null != memberInfo) {
            CustomUser user = new CustomUser();
            user.setId(memberInfo.getMemberId().toString());
            user.setFirstName(memberInfo.getMemberName());
            return user;
        }

        return null;
    }

    @Override
    public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
        if (null != query.getId()) {
            User user = findUserById(query.getId());
            return Arrays.asList(user);
        }

        if (null != query.getGroupId()) {
            List<MemberBasicInfoDto> memberList = findMemberListByPostId(query.getGroupId());
            if (null != memberList && memberList.size() > 0) {
                List<User> result = new ArrayList<>();
                CustomUser user = null;
                for (MemberBasicInfoDto item : memberList) {
                    user = new CustomUser();
                    user.setId(item.getMemberId().toString());
                    user.setFirstName(item.getMemberName());
                    result.add(user);
                }

                return result;
            }
        }

        return null;
    }

    @Override
    public List<Group> findGroupsByUser(String userId) {
        MemberBasicInfoDto memberInfo = findMemberInfoByMemberId(userId);
        if (null != memberInfo) {
            CustomGroup group = new CustomGroup(memberInfo.getPostId().toString(), memberInfo.getPostName().toString());
            return Arrays.asList(group);
        }

        return null;
    }

    /**
     * 根据成员Id获取成员信息
     *
     * @param memberId
     * @return
     */
    public MemberBasicInfoDto findMemberInfoByMemberId(String memberId) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            Long id = Long.valueOf(memberId);

            MemberBasicInfoDto member = memberQueryService.queryMemberInfoByMemberId(identity.getTenantCode(), identity.getProductCode(), id);
            if (null != member) {
                return member;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    /**
     * 根据岗位Id获取成员列表
     *
     * @param postId
     * @return
     */
    public List<MemberBasicInfoDto> findMemberListByPostId(String postId) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            Long id = Long.valueOf(postId);

            List<MemberBasicInfoDto> list = memberQueryService.queryMemberListByPostId(identity.getTenantCode(), identity.getProductCode(), id);
            if (null != list) {
                return list;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    /**
     * 获取成员所在组织的成员信息
     *
     * @param memberId
     * @return
     */
    public List<MemberBasicInfoDto> queryOrgMemberListByMemberId(String memberId) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            Long id = Long.valueOf(memberId);

            List<MemberBasicInfoDto> list = memberQueryService.queryOrgMemberListByMemberId(identity.getTenantCode(), identity.getProductCode() , id);
            return list;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 获取成员所在组织的上级组织的成员信息
     *
     * @param memberId
     * @return
     */
    public List<MemberBasicInfoDto> queryParentOrgMemberListByMemberId(String memberId) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            Long id = Long.valueOf(memberId);

            List<MemberBasicInfoDto> list = memberQueryService.queryParentOrgMemberListByMemberId(identity.getTenantCode(), identity.getProductCode(), id);
            return list;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 获取成员所在组织的上上级组织的成员信息
     *
     * @param memberId
     * @return
     */
    public List<MemberBasicInfoDto> queryPParentOrgMemberListByMemberId(String memberId) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            Long id = Long.valueOf(memberId);

            List<MemberBasicInfoDto> list = memberQueryService.queryPParentOrgMemberListByMemberId(identity.getTenantCode(), identity.getProductCode(), id);
            return list;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 获取成员所在组织的同级组织的成员信息
     *
     * @param memberId
     * @return
     */
    public List<MemberBasicInfoDto> queryPeerOrgMemberListByMemberId(String memberId) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            Long id = Long.valueOf(memberId);

            List<MemberBasicInfoDto> list = memberQueryService.queryPeerOrgMemberListByMemberId(identity.getTenantCode(), identity.getProductCode(), id);
            return list;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 获取成员所在岗位的上级岗位的成员信息
     *
     * @param memberId
     * @return
     */
    public List<MemberBasicInfoDto> queryParentPostMemberListByMemberId(String memberId) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            Long id = Long.valueOf(memberId);

            List<MemberBasicInfoDto> list = memberQueryService.queryParentPostMemberListByMemberId(identity.getTenantCode(), identity.getProductCode(), id);
            return list;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 根据岗位Id集合批量获取成员信息
     *
     * @param posts
     * @return
     */
    public List<MemberBasicInfoDto> batchQueryMemberListByPosts(Collection<String> posts) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            List<Long> list = new ArrayList<>();
            for (Iterator<String> iterator = posts.iterator(); iterator.hasNext(); ) {
                list.add(Long.valueOf(iterator.next()));
            }

            return memberQueryService.batchQueryMemberListByPosts(identity.getTenantCode(), identity.getProductCode(), list);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    /**
     * 根据组织Id集合批量获取成员信息
     *
     * @param orgs
     * @return
     */
    public List<MemberBasicInfoDto> batchQueryMemberListByOrgs(Collection<String> orgs) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            List<Long> list = new ArrayList<>();
            for (Iterator<String> iterator = orgs.iterator(); iterator.hasNext(); ) {
                list.add(Long.valueOf(iterator.next()));
            }

            return memberQueryService.batchQueryMemberListByOrgs(identity.getTenantCode(), identity.getProductCode(), list);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    /**
     * 根据成员Id集合批量获取成员信息
     *
     * @param members
     * @return
     */
    public List<MemberBasicInfoDto> batchQueryMemberInfo(Collection<String> members) {
        try {
            TenantIdentity identity = ProcessContext.getCurrentTenantIdentity();
            List<Long> list = new ArrayList<>();
            for (Iterator<String> iterator = members.iterator(); iterator.hasNext(); ) {
                list.add(Long.valueOf(iterator.next()));
            }

            return memberQueryService.batchQueryMemberInfo(identity.getTenantCode(), identity.getProductCode(), list);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

}
