package com.example.dietcommunity.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.MemberException;
import com.example.dietcommunity.common.security.JwtTokenProvider;
import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.model.request.SignUpGeneralRequest;
import com.example.dietcommunity.member.repository.MemberRepository;
import com.example.dietcommunity.member.repository.MemberTokenRedisRepository;
import com.example.dietcommunity.member.type.MemberRole;
import com.example.dietcommunity.member.type.MemberStatus;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private JwtTokenProvider jwtTokenProvider;
  @Mock
  private MemberTokenRedisRepository memberTokenRedisRepository;


  //  @Spy
  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private MemberService memberService;

  private Member fakeMember;
  private BCryptPasswordEncoder encoder;
  /*
  PasswordEncoder 사용 시 null 로 반환, dataSetup 에서도 encode를 사용해서 verify 횟수가 실제 서비스로직과 달라지는 문제때문에
  테스트클래스 내에서 직접 사용할 encoder를 따로 만들어줌
 */


  @BeforeEach
  public void dataSetup() {

    encoder = new BCryptPasswordEncoder();

    fakeMember = Member.builder()
        .memberId(1L)
        .email("fakeId1@naver.com")
        .accountId("fakeAccountId1")
        .nickname("fake닉네임1")
        .password(encoder.encode("fakePassword1!"))
        .selfIntroduction("자기소개를 입력해주세요.")
        .finalGoalWeight(null)
        .status(MemberStatus.UNAUTHENTICATED)
        .role(MemberRole.GENERAL)
        .registeredAt(LocalDateTime.now())
        .modifiedAt(null)
        .build();

  }


  @DisplayName("회원가입성공")
  @Test
  void signUpGeneralMember_success() {

    // given
    SignUpGeneralRequest request = SignUpGeneralRequest.builder()
        .email("fakeId1@naver.com")
        .accountId("fakeAccountId1")
        .nickname("fake닉네임1")
        .password("fakePassword1!")
        .build();

    String encodedPassword = encoder.encode(request.getPassword());

    when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(memberRepository.existsByAccountId(request.getAccountId())).thenReturn(false);
    when(memberRepository.existsByNickname(request.getNickname())).thenReturn(false);

    when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
    when(memberRepository.save(any(Member.class))).thenReturn(fakeMember);

    ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);

    // when
    Member member = memberService.signUpGeneralMember(request);

    // then
    verify(memberRepository, times(1)).existsByEmail(request.getEmail());
    verify(memberRepository, times(1)).existsByAccountId(request.getAccountId());
    verify(memberRepository, times(1)).existsByNickname(request.getNickname());

    verify(passwordEncoder, times(1)).encode(request.getPassword());
    verify(memberRepository, times(1)).save(memberCaptor.capture());

    Member capturedMember = memberCaptor.getValue();
    assertEquals(request.getEmail(), capturedMember.getEmail());
    assertEquals(request.getAccountId(), capturedMember.getAccountId());
    assertEquals(request.getNickname(), capturedMember.getNickname());
    assertEquals(encodedPassword, capturedMember.getPassword());
  }


  @DisplayName("회원가입실패 - 이미 사용중인 이메일 입니다.")
  @Test
  void signUpGeneralMember_fail_email() {

    // given
    SignUpGeneralRequest request = SignUpGeneralRequest.builder()
        .email("fakeId1@naver.com")
        .accountId("fakeAccountId1")
        .nickname("fake닉네임1")
        .password("fakePassword1!")
        .build();


    when(memberRepository.existsByEmail(request.getEmail())).thenReturn(true);

    // when
    MemberException memberException = assertThrows(MemberException.class,
        () -> memberService.signUpGeneralMember(request));

    // then
    verify(memberRepository, times(1)).existsByEmail(request.getEmail());
    verify(memberRepository, times(0)).existsByAccountId(request.getAccountId());
    verify(memberRepository, times(0)).existsByNickname(request.getNickname());

    verify(passwordEncoder, times(0)).encode(request.getPassword());
    verify(memberRepository, times(0)).save(any(Member.class));

    assertEquals(ErrorCode.ALREADY_USING_EMAIL, memberException.getErrorCode());
  }



  @DisplayName("회원가입실패 - 이미 사용중인 아이디입니다.")
  @Test
  void signUpGeneralMember_fail_accountId() {

    // given
    SignUpGeneralRequest request = SignUpGeneralRequest.builder()
        .email("fakeId1@naver.com")
        .accountId("fakeAccountId1")
        .nickname("fake닉네임1")
        .password("fakePassword1!")
        .build();


    when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(memberRepository.existsByAccountId(request.getAccountId())).thenReturn(true);

    // when
    MemberException memberException = assertThrows(MemberException.class,
        () -> memberService.signUpGeneralMember(request));

    // then
    verify(memberRepository, times(1)).existsByEmail(request.getEmail());
    verify(memberRepository, times(1)).existsByAccountId(request.getAccountId());
    verify(memberRepository, times(0)).existsByNickname(request.getNickname());

    verify(passwordEncoder, times(0)).encode(request.getPassword());
    verify(memberRepository, times(0)).save(any(Member.class));

    assertEquals(ErrorCode.ALREADY_USING_ACCOUNT_ID, memberException.getErrorCode());
  }


  @DisplayName("회원가입실패 - 이미 사용중인 닉네임 입니다.")
  @Test
  void signUpGeneralMember_fail_nickname() {

    // given
    SignUpGeneralRequest request = SignUpGeneralRequest.builder()
        .email("fakeId1@naver.com")
        .accountId("fakeAccountId1")
        .nickname("fake닉네임1")
        .password("fakePassword1!")
        .build();


    when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(memberRepository.existsByAccountId(request.getAccountId())).thenReturn(false);
    when(memberRepository.existsByNickname(request.getNickname())).thenReturn(true);

    // when
    MemberException memberException = assertThrows(MemberException.class,
        () -> memberService.signUpGeneralMember(request));

    // then
    verify(memberRepository, times(1)).existsByEmail(request.getEmail());
    verify(memberRepository, times(1)).existsByAccountId(request.getAccountId());
    verify(memberRepository, times(1)).existsByNickname(request.getNickname());

    verify(passwordEncoder, times(0)).encode(request.getPassword());
    verify(memberRepository, times(0)).save(any(Member.class));

    assertEquals(ErrorCode.ALREADY_USING_NICKNAME, memberException.getErrorCode());
  }
}