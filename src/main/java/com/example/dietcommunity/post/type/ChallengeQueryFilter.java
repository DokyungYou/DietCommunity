package com.example.dietcommunity.post.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChallengeQueryFilter {

  WHOLE("전체"),
  ABLE_APPLY("신청 가능");


  private final String description;

}
