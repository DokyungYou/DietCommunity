package com.example.dietcommunity.member.service;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.MemberException;
import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.model.request.SignUpGeneralRequest;
import com.example.dietcommunity.member.repository.MemberRepository;
import com.example.dietcommunity.member.type.MemberRole;
import com.example.dietcommunity.member.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;


  /**
   * 회원가입(일반)
   * @param request
   * @return Member
   */
  @Transactional
  public Member signUpGeneralMember(SignUpGeneralRequest request) {
    //유니크 값들 확인
    if (memberRepository.existsByEmail(request.getEmail())) {
      throw new MemberException(ErrorCode.ALREADY_USING_EMAIL);
    }
    if (memberRepository.existsByAccountId(request.getAccountId())) {
      throw new MemberException(ErrorCode.ALREADY_USING_ACCOUNT_ID);
    }
    if (memberRepository.existsByNickname(request.getNickname())) {
      throw new MemberException(ErrorCode.ALREADY_USING_NICKNAME);
    }

    return memberRepository.save(
        Member.builder()
            .email(request.getEmail())
            .accountId(request.getAccountId())
            .nickname(request.getNickname())
            .password(passwordEncoder.encode(request.getPassword()))
            .finalGoalWeight(0)
            .selfIntroduction("간단한 소개글을 적어주세요.")
            .status(MemberStatus.UNAUTHENTICATED)
            .role(MemberRole.GENERAL)
            .build());
  }


}
