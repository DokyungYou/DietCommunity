package com.example.dietcommunity.common.security;

import com.example.dietcommunity.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class MemberDetails implements UserDetails {

  // 민감한 정보는 직렬화할때만 사용되도록 지정
  private Member member;
  


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collection = new ArrayList<>();

    collection.add( new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return member.getRole().name();
      }
    });

    return collection;
  }

  
  @JsonProperty(access = Access.WRITE_ONLY)
  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @JsonProperty(access = Access.WRITE_ONLY)
  @Override
  public String getUsername() { // 고유한 식별값 반환
    return member.getEmail();
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
