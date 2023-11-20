package com.example.dietcommunity.member.controller;

import com.example.dietcommunity.common.mail.EmailService;
import com.example.dietcommunity.common.redis.RedisEmailService;
import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.model.request.SignUpGeneralRequest;
import com.example.dietcommunity.member.model.response.SignUpGeneralResponse;
import com.example.dietcommunity.member.service.MemberService;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class MemberController {

  private final MemberService memberService;
  private final EmailService emailService;
  private final RedisEmailService redisEmailService;

  @PostMapping("/sign-up/general")
  public ResponseEntity<SignUpGeneralResponse> signUpGeneral(@Valid @RequestBody SignUpGeneralRequest request) {
    String authCode = String.valueOf(UUID.randomUUID());
    Member member = memberService.signUpGeneralMember(request);

    redisEmailService.saveAuthEmailCode(member.getEmail(), authCode);
    emailService.sendAuthEmail(member.getEmail(), authCode);

    return ResponseEntity.ok(new SignUpGeneralResponse(member));
  }


}
