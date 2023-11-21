package com.example.dietcommunity.common.security;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.SecurityExceptionCustom;
import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MemberDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(username)
        .orElseThrow(() -> new SecurityExceptionCustom(ErrorCode.NOT_FOUND_MEMBER)); // 필터에서 돌아가는 부분이라 시큐리티 예외로 던졌음

    return new MemberDetails(member);
  }

}
