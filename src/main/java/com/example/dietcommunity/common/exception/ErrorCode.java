package com.example.dietcommunity.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // common
  INVALID_INPUT_VALUE(400,"유효하지 않은 입력값이 있습니다."),
  ILLEGAL_ARGUMENT_EXCEPTION( 400, "IllegalArgumentException 이 발생하였습니다."),
  NULL_POINTER_EXCEPTION(500, "NullPointerException 이 발생하였습니다."),


  // SecurityException
  NOT_FOUND_TOKEN_SET(401,"해당 accessToken으로 저장된 token을 찾을 수 없습니다."),
  INVALID_REFRESH_TOKEN(401,"재로그인해주세요. [RefreshToken 만료]"),
  INVALID_TOKEN(401,"유효하지 않은 토큰입니다."),
  LOGOUT_OUT(401,"로그아웃 상태인 계정입니다."),



  // MailException
  FAIL_TO_SEND_MAIL(500,"이메일 전송 실패, 다시 시도해주세요."),

  // InvalidAuthCodeException
  INVALID_AUTH_CODE(400,"인증코드가 일치하지않거나 만료되었습니다."),


  // PostException
  NON_EXISTENT_CATEGORY(404, "존재하지 않는 카테고리입니다."),
  INVALID_CATEGORY_REQUEST(400, "잘못된 카테고리 요청입니다."),
//  UNSUPPORTED_FILE_FORMAT(400, "지원되지 않는 형식의 파일입니다."),
  NOT_FOUND_POST(404, "존재하지 않는 게시글입니다."),
  CANNOT_EDIT_OTHERS_POST(400, "다른 사용자의 게시물을 편집 또는 삭제할 수 없습니다."),

  CANNOT_EDIT_CHALLENGE_PASSED_START_DATE(400, "이미 시작날짜가 지난 챌린지는 수정이 불가합니다." ),



  // MemberException
  ALREADY_USING_EMAIL(409,"이미 사용중인 이메일 입니다."),  // 이미 점유한 유니크 자원과의 충돌
  ALREADY_USING_NICKNAME(409,"이미 사용중인 닉네임 입니다."),
  ALREADY_USING_ACCOUNT_ID(409,"이미 사용중인 아이디입니다."),
  NOT_FOUND_MEMBER(404,"해당 사용자를 찾을 수 없습니다."),
  ALREADY_AUTHENTICATED_MEMBER(409,"이미 인증을 완료한 사용자입니다."), // 현 상태와 요청의 충돌
  NOT_CORRECT_ACCOUNT_ID_OR_PASSWORD(401,"아이디 또는 비밀번호를 잘못 입력했습니다."),
  UNABLE_LOGIN_UNAUTHENTICATED_MEMBER(403,"계정인증을 완료한 계정만 로그인 할 수 있습니다."),
  ALREADY_FOLLOWING_MEMBER(409,"이미 팔로잉한 회원입니다.")
  ;


  private final int httpStatus;
  private final String description;
}
