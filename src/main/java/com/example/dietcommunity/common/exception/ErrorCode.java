package com.example.dietcommunity.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // common
  INVALID_INPUT_VALUE("유효하지 않은 입력값이 있습니다."),


  // SecurityException
  NOT_FOUND_TOKEN_SET("해당 accessToken으로 저장된 token을 찾을 수 없습니다."),
  INVALID_REFRESH_TOKEN("재로그인해주세요. [RefreshToken 만료]"),
  INVALID_TOKEN("유효하지 않은 토큰입니다."),
  LOGOUT_OUT("로그아웃 상태인 계정입니다."),



  // MailException
  FAIL_TO_SEND_MAIL("이메일 전송 실패, 다시 시도해주세요."),

  // InvalidAuthCodeException
  INVALID_AUTH_CODE("인증코드가 일치하지않거나 만료되었습니다."),




  // MemberException
  ALREADY_USING_EMAIL("이미 사용중인 이메일 입니다."),
  ALREADY_USING_NICKNAME("이미 사용중인 닉네임 입니다."),
  ALREADY_USING_ACCOUNT_ID("이미 사용중인 아이디입니다."),
  NOT_FOUND_MEMBER("해당 사용자를 찾을 수 없습니다."),
  ALREADY_AUTHENTICATED_MEMBER("이미 인증을 완료한 사용자입니다."),
  NOT_CORRECT_ACCOUNT_ID_OR_PASSWORD("아이디 또는 비밀번호를 잘못 입력했습니다."),
  UNABLE_LOGIN_UNAUTHENTICATED_MEMBER("계정인증을 완료한 계정만 로그인 할 수 있습니다.")

  ;


  private final String description;
}
