package com.example.dietcommunity.post.model;

import com.example.dietcommunity.post.entity.Challenge;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

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


  public static Page<ChallengeDto> toDtoPage(Page<Challenge> challengePage) {

    return challengePage.map(ChallengeDto::fromChallenge);
  }

  public static ChallengeDto fromChallenge(Challenge challenge) {
    return ChallengeDto.builder()
        .id(challenge.getId())
        .challengeStartDate(challenge.getChallengeStartDate())
        .challengeEndDate(challenge.getChallengeEndDate())
        .limitApplicantsNumber(challenge.getLimitApplicantsNumber())
        .participateChannelId(challenge.getParticipateChannelId())
        .isFilled(challenge.isFilled())
        .postDto(PostDto.fromPost(challenge.getPost()))
        .build();
  }

}
