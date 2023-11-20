package com.example.dietcommunity.member.model.request;


import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginGeneralRequest {

  @NotBlank(message = "아이디를 입력해주세요.")
  private String accountId;

  @NotBlank(message = "비밀번호를 입력해주세요.")
  private String password;

}
