package com.example.dietcommunity.member.service;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.MemberException;
import com.example.dietcommunity.common.exception.SecurityExceptionCustom;
import com.example.dietcommunity.common.security.JwtTokenProvider;
import com.example.dietcommunity.common.security.MemberDetails;
import com.example.dietcommunity.member.entity.Following;
import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.entity.MemberAuthToken;
import com.example.dietcommunity.member.model.request.LoginGeneralRequest;
import com.example.dietcommunity.member.model.request.SignUpGeneralRequest;
import com.example.dietcommunity.member.model.response.FollowingDto;
import com.example.dietcommunity.member.repository.FollowingRepository;
import com.example.dietcommunity.member.repository.MemberRepository;
import com.example.dietcommunity.member.repository.MemberTokenRedisRepository;
import com.example.dietcommunity.member.type.FollowingType;
import com.example.dietcommunity.member.type.MemberRole;
import com.example.dietcommunity.member.type.MemberStatus;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

  private final PasswordEncoder passwordEncoder;
  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final MemberTokenRedisRepository memberTokenRedisRepository;
  private final FollowingRepository followingRepository;


  /**
   * 회원가입(일반)
   *
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


  /**
   * 계정인증메일 재전송 가능여부검증
   *
   * @param email
   */
  public void validateResendAuthMemberEmail(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

    if (member.getStatus() == MemberStatus.ACTIVATED) {
      throw new MemberException(ErrorCode.ALREADY_AUTHENTICATED_MEMBER);
    }
  }

  /**
   * 일반 로그인
   *
   * @param request
   * @return
   */
  @Transactional
  public Pair<Member, MemberAuthToken> loginGeneral(LoginGeneralRequest request) {

    // 보안상 아이디, 비밀번호 어떤 것이 틀렸는지 알 수 없도록 같은 예외메세지 사용
    Member member = memberRepository.findByAccountId(request.getAccountId())
        .orElseThrow(() -> new MemberException(ErrorCode.NOT_CORRECT_ACCOUNT_ID_OR_PASSWORD));

    if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
      throw new MemberException(ErrorCode.NOT_CORRECT_ACCOUNT_ID_OR_PASSWORD);
    }

    // 계정인증을 완료한 사람만 로그인 가능
    if (member.getStatus() == MemberStatus.UNAUTHENTICATED) {
      throw new MemberException(ErrorCode.UNABLE_LOGIN_UNAUTHENTICATED_MEMBER);
    }

    // 꺼내온 멤버를 토대로 토큰 생성, 레디스에 저장
    String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
    String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

    MemberAuthToken savedToken = memberTokenRedisRepository.save(new MemberAuthToken(accessToken, refreshToken));

    return Pair.of(member, savedToken);
  }


  @Transactional
  public void logout(String accessToken) {

    MemberAuthToken memberAuthToken = memberTokenRedisRepository.findByAccessToken(accessToken)
        .orElseThrow(() -> new SecurityExceptionCustom(ErrorCode.NOT_FOUND_TOKEN_SET));

    memberTokenRedisRepository.delete(memberAuthToken);

  }


  @Transactional
  public String setTemporaryPassword(String email, String accountId) {
    Member member = memberRepository.findByEmailAndAccountId(email, accountId)
        .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

    String temporaryPassword = createTemporaryPassword();

    member.updatePassword(passwordEncoder.encode(temporaryPassword));
    memberRepository.save(member);

    return temporaryPassword;
  }


  private String createTemporaryPassword() {
    StringBuilder sb = new StringBuilder();
    int passwordLength = ThreadLocalRandom.current().nextInt(8, 15 + 1);
    for (int i = 0; i < passwordLength; i++) {
      sb.append((char) ThreadLocalRandom.current().nextInt(33, 127));
    }
    return sb.toString();

  }

  public String getMemberAccountId(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

    return member.getAccountId();
  }

  @Transactional
  public Member withdrawMember(MemberDetails memberDetails) {

    Member member = memberDetails.toMember();
    member.withdrawMember();

    return memberRepository.save(member);
  }

//  public Member getMemberProfile(long memberId) {
//    return memberRepository.findById(memberId)
//        .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
//  }

  @Transactional
  public void followMember(MemberDetails memberDetails, long followeeId) {

    if(followingRepository.existsByFollower_IdAndFollowee_Id(memberDetails.getMemberId(), followeeId)){
      throw new MemberException(ErrorCode.ALREADY_FOLLOWING_MEMBER);
    }

    Member followingMember = memberRepository.findById(followeeId)
        .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

    if (followingMember.getStatus() != MemberStatus.ACTIVATED) {
      throw new MemberException(ErrorCode.NOT_FOUND_MEMBER);
    }


    followingRepository.save(
        Following.builder()
        .follower(memberDetails.toMember())
        .followee(followingMember)
        .build());

  }

  @Transactional
  public void removeFollowing(MemberDetails memberDetails, long followeeId){

    followingRepository.deleteByFollower_IdAndFollowee_Id(memberDetails.getMemberId(), followeeId);

  }

  // memberId, 닉네임
  public Slice<FollowingDto> getFollowings(Long memberId, FollowingType followingType ,Long lastViewDtoNum) {

    return followingRepository.getFollowings(memberId, followingType ,lastViewDtoNum,Pageable.ofSize(20));
  }
}

