package com.example.dietcommunity.post.model;

import com.example.dietcommunity.post.entity.Category;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

  private Long postId;
  private Category category;
  private String writerNickname;
  private String title;
  private long totalHits;
  private long totalLikes;
  private LocalDateTime registeredAt;


}
