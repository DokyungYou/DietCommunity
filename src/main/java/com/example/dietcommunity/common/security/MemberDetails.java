package com.example.dietcommunity.common.security;

import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.type.MemberRole;
import com.example.dietcommunity.member.type.MemberStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@AllArgsConstructor
public class MemberDetails implements UserDetails {


  private Long memberId;

  private String email;

  private String accountId;

  private String nickname;

  private String password;

  private String selfIntroduction;

  private Integer finalGoalWeight;

  private MemberStatus status;

  private MemberRole role;

  private LocalDateTime registeredAt;

  private LocalDateTime modifiedAt;


  public static MemberDetails fromMember(Member member) {
    return MemberDetails.builder()
        .memberId(member.getMemberId())
        .email(member.getEmail())
        .accountId(member.getAccountId())
        .nickname(member.getNickname())
        .password(member.getPassword())
        .selfIntroduction(member.getSelfIntroduction())
        .finalGoalWeight(member.getFinalGoalWeight())
        .status(member.getStatus())
        .role(member.getRole())
        .registeredAt(member.getRegisteredAt())
        .modifiedAt(member.getModifiedAt())
        .build();
  }


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collection = new ArrayList<>();

    collection.add(new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return role.name();
      }
    });

    return collection;
  }


  @JsonProperty(access = Access.WRITE_ONLY)
  @Override
  public String getPassword() {
    return this.password;
  }

  @JsonProperty(access = Access.WRITE_ONLY)
  @Override
  public String getUsername() { // 고유한 식별값 반환
    return this.getEmail();
  }

  @JsonProperty(access = Access.WRITE_ONLY)
  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @JsonProperty(access = Access.WRITE_ONLY)
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonProperty(access = Access.WRITE_ONLY)
  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @JsonProperty(access = Access.WRITE_ONLY)
  @Override
  public boolean isEnabled() {
    return true;
  }
}
