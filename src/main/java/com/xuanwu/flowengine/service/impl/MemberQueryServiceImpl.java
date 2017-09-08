package com.xuanwu.flowengine.service.impl;

import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;
import com.xuanwu.flowengine.service.MemberQueryService;

import java.util.List;

/**
 * 功能描述
 * <p>
 * author：Created by ttan on 2017/9/8 0008.
 */
public class MemberQueryServiceImpl implements MemberQueryService{
    @Override
    public MemberBasicInfoDto queryMemberInfoByMemberId(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public MemberBasicInfoDto queryPostInfoByPostId(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> queryOrgMemberListByMemberId(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> queryMemberListByPostId(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> queryParentOrgMemberListByMemberId(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> queryPParentOrgMemberListByMemberId(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> queryPeerOrgMemberListByMemberId(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> queryParentPostMemberListByMemberId(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> batchQueryMemberListByPosts(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> batchQueryMemberListByPosts(Long tenantCode, Long productCode, List<Long> id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> batchQueryMemberListByOrgs(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> batchQueryMemberListByOrgs(Long tenantCode, Long productCode, List<Long> id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> batchQueryMemberInfo(Long tenantCode, Long productCode, Long id) {
        return null;
    }

    @Override
    public List<MemberBasicInfoDto> batchQueryMemberInfo(Long tenantCode, Long productCode, List<Long> id) {
        return null;
    }
}
