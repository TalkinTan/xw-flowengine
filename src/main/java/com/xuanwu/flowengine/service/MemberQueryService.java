package com.xuanwu.flowengine.service;

import com.xuanwu.flowengine.entity.dto.MemberBasicInfoDto;

import java.util.List;

/**
 * 功能描述
 * <p>
 * author：Created by ttan on 2017/9/8 0008.
 */
public interface MemberQueryService {

    MemberBasicInfoDto queryMemberInfoByMemberId(Long tenantCode, Long productCode, Long id);

    MemberBasicInfoDto queryPostInfoByPostId(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> queryOrgMemberListByMemberId(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> queryMemberListByPostId(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> queryParentOrgMemberListByMemberId(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> queryPParentOrgMemberListByMemberId(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> queryPeerOrgMemberListByMemberId(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> queryParentPostMemberListByMemberId(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> batchQueryMemberListByPosts(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> batchQueryMemberListByPosts(Long tenantCode, Long productCode, List<Long> id);

    List<MemberBasicInfoDto> batchQueryMemberInfo(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> batchQueryMemberInfo(Long tenantCode, Long productCode, List<Long> id);

    List<MemberBasicInfoDto> batchQueryMemberListByOrgs(Long tenantCode, Long productCode, Long id);

    List<MemberBasicInfoDto> batchQueryMemberListByOrgs(Long tenantCode, Long productCode, List<Long> id);

}
