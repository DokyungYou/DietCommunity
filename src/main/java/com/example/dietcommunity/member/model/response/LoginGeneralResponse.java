package com.example.dietcommunity.member.model.response;

import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.entity.MemberAuthToken;
import com.example.dietcommunity.member.type.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;


@Getter
@Builder
@Setter
@AllArgsConstructor
public class LoginGeneralResponse {

  private Long memberId;
  private String nickname;
  private String accessToken;
  private String refreshToken;
  private MemberRole memberRole;

  public static LoginGeneralResponse fromMemberAuthPair(Pair<Member, MemberAuthToken> loginResponse) {

    Member member = loginResponse.getFirst();
    MemberAuthToken memberAuthToken = loginResponse.getSecond();

    return LoginGeneralResponse.builder()
        .memberId(member.getMemberId())
        .nickname(member.getNickname())
        .accessToken(memberAuthToken.getAccessToken())
        .refreshToken(memberAuthToken.getRefreshToken())
        .memberRole(member.getRole())
        .build();

  }
}
