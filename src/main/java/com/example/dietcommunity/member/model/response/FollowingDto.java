package com.example.dietcommunity.member.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowingDto {

  private long followingId;  // 페이징 위함
  private long memberId;
  private String nickname;

}
