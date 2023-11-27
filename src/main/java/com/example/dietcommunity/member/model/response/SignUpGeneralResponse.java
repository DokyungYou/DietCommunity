package com.example.dietcommunity.member.model.response;

import com.example.dietcommunity.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpGeneralResponse {

  private String email;
  private String accountId;
  private String nickname;


  public SignUpGeneralResponse(Member member) {
    this.email = member.getEmail();
    this.accountId = member.getAccountId();
    this.nickname = member.getNickname();
  }
}
