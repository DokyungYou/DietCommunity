package com.example.dietcommunity.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@RedisHash(timeToLive = 60 * 60 * 24 * 14) // 2주
public class MemberAuthToken {

  @Id
  private Long id;
  @Indexed
  private String accessToken;
  private String refreshToken;

  @Builder
  public MemberAuthToken(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
