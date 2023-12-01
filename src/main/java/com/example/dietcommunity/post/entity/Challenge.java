package com.example.dietcommunity.post.entity;

import com.example.dietcommunity.post.model.ChallengeWriteDto;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Challenge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.EAGER)
  private Post post;

  @Column(nullable = false)
  private LocalDate challengeStartDate;

  @Column(nullable = false)
  private LocalDate challengeEndDate;

  private int limitApplicantsNumber;

  private long participateChannelId;

  private boolean isFilled;

  public Challenge updateChallenge(ChallengeWriteDto.Request request, Post updatedPost){

    this.post = updatedPost;
    this.challengeStartDate = request.getStartDate();
    this.challengeEndDate = request.getEndDate();
    this.limitApplicantsNumber = request.getLimitApplicantsNumber();

    return this;
  }

}
