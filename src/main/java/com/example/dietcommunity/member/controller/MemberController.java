package com.example.dietcommunity.member.controller;

import com.example.dietcommunity.common.mail.EmailService;
import com.example.dietcommunity.common.mail.RedisEmailService;
import com.example.dietcommunity.common.security.MemberDetails;
import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.model.request.FindPasswordRequest;
import com.example.dietcommunity.member.model.request.LoginGeneralRequest;
import com.example.dietcommunity.member.model.request.SignUpGeneralRequest;
import com.example.dietcommunity.member.model.response.LoginGeneralResponse;
import com.example.dietcommunity.member.model.response.SignUpGeneralResponse;
import com.example.dietcommunity.member.service.MemberService;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

  private final MemberService memberService;
  private final EmailService emailService;
  private final RedisEmailService redisEmailService;



  @PostMapping("/signup-general")
  public ResponseEntity<SignUpGeneralResponse> signUpGeneral(@Valid @RequestBody SignUpGeneralRequest request) {
    String authCode = String.valueOf(UUID.randomUUID());
    Member member = memberService.signUpGeneralMember(request);

    redisEmailService.saveAuthEmailCode(member.getEmail(), authCode);
    emailService.sendAuthEmailMessage(member.getEmail(), authCode);

    return ResponseEntity.ok(new SignUpGeneralResponse(member));
  }


  /**
   * 계정인증 (이메일로 전송될 url)
   *
   * @param email
   * @param authCode
   * @return
   */
  @GetMapping("/authentication")
  public ResponseEntity<Void> authenticateEmail(
      @RequestParam(name = "email") String email,
      @RequestParam(name = "authCode") String authCode
  ) {

    redisEmailService.validateAuthEmailCode(email, authCode);
    return ResponseEntity.ok().build();
  }


  /**
   * 계정인증 이메일 전송 (재전송용)
   * 해당이메일로 전송 X or 인증코드 만료 시 재발송
   * @return
   */
  @GetMapping("/resending-authentication-email")
  public ResponseEntity<Void> sendEmailAuthenticateMember(
      @RequestParam(name = "email") String email) {

    String authCode = String.valueOf(UUID.randomUUID());
    memberService.validateResendAuthMemberEmail(email);

    redisEmailService.deleteAuthEmail(email);
    redisEmailService.saveAuthEmailCode(email, authCode);
    emailService.sendAuthEmailMessage(email, authCode);

    return ResponseEntity.ok().build();
  }

  /**
   * 일반 로그인
   *
   * @param request
   * @return
   */
  @PostMapping("/login-general")
  public ResponseEntity<LoginGeneralResponse> loginGeneral(@Valid @RequestBody LoginGeneralRequest request) {

    return ResponseEntity
        .ok(LoginGeneralResponse.fromMemberAuthPair(memberService.loginGeneral(request)));
  }

  @GetMapping("/logout")
  public ResponseEntity<Void> logout(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @RequestHeader("Authorization") String accessToken
  ) {

    memberService.logout(accessToken);
    log.info("사용자가 로그아웃했습니다. memberId: {}", memberDetails.getMemberId());

    return ResponseEntity.ok().build();
  }



  @GetMapping("/help/finding-id")
  public ResponseEntity<Void> findAccountId(@RequestParam String email) {

    emailService.sendFindAccountIdMessage(email,memberService.getMemberAccountId(email));
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/help/finding-password")
  public ResponseEntity<Void> findPassword(@Valid @RequestBody FindPasswordRequest request) {

    String temporaryPassword = memberService.setTemporaryPassword(request.getEmail(),request.getAccountId());
    emailService.sendFindPasswordMessage(request.getEmail(),temporaryPassword);

    return ResponseEntity.ok().build();
  }


}
