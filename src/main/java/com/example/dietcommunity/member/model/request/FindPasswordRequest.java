package com.example.dietcommunity.member.model.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FindPasswordRequest {

  @NotBlank(message = "이메일을 입력해주세요.")
  String email;

  @NotBlank(message = "아이디를 입력해주세요.")
  String accountId;
}
