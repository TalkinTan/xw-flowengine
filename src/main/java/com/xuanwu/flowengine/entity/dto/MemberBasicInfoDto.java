package com.xuanwu.flowengine.entity.dto;

/**
 * 功能描述
 * <p>
 * author：Created by ttan on 2017/9/8 0008.
 */
public class MemberBasicInfoDto {
    private String memberId;

    private String memberName;

    private String postId;

    private String postName;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }
}
