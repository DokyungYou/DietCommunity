package com.example.dietcommunity.post.model;

import com.example.dietcommunity.post.entity.Challenge;
import com.example.dietcommunity.post.entity.Post;
import com.example.dietcommunity.post.entity.PostImage;
import com.example.dietcommunity.post.type.PostStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class ChallengeWriteDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request{

    @NotNull(message = "카테고리를 선택해주세요.")
    private long categoryId;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String contents;

    @NotNull(message = "시작날짜를 선택해주세요.")
    @Future(message = "시작날짜는 현재날짜 이후여야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "마감날짜를 선택해주세요.")
    @Future(message = "마감날짜는 현재날짜 이후여야 합니다.")
    private LocalDate endDate;

    @NotNull(message = "제한인원수를 입력해주세요.")
    private int limitApplicantsNumber;



    public void validateInputDate() {
      if(!startDate.isBefore(endDate)){
        throw new IllegalArgumentException("챌린지 시작날짜는 마감날짜보다 이전이어야합니다.");
      }
    }

  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response{

    private long postId;
    private String title;
    private String contents;
    private PostStatus postStatus;
    private LocalDateTime registeredAt;
    private List<String> imageUrls;

    private LocalDate startDate;
    private LocalDate endDate;
    private int limitApplicantsNumber;
    private long participateChannelId;

    public static ChallengeWriteDto.Response of(Post post, List<PostImage> images, Challenge challenge) {

      List<String> imageUrls = images.stream()
          .map(PostImage::getImageUrl)
          .collect(Collectors.toList());


      return ChallengeWriteDto.Response.builder()
          .postId(post.getId())
          .title(post.getTitle())
          .contents(post.getContents())
          .postStatus(post.getPostStatus())
          .registeredAt(post.getRegisteredAt())
          .imageUrls(imageUrls)

          .startDate(challenge.getChallengeStartDate())
          .endDate(challenge.getChallengeEndDate())
          .limitApplicantsNumber(challenge.getLimitApplicantsNumber())
          .participateChannelId(challenge.getParticipateChannelId())
          .build();
    }
  }
}
