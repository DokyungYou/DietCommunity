package com.example.dietcommunity.post.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeDto {

  private Long id;
  private LocalDate challengeStartDate;
  private LocalDate challengeEndDate;
  private int limitApplicantsNumber;
  private long participateChannelId;
  private boolean isFilled;

  private PostDto postDto;

}
