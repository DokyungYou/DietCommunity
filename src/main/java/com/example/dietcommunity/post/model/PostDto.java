package com.example.dietcommunity.post.model;

import com.example.dietcommunity.post.entity.Post;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

  private Long postId;
  private String writerNickname;
  private String title;
  private long totalHits;
  private long totalLikes;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;


  public static Page<PostDto> toDtoPage(Page<Post> postPage) {

    return postPage.map(PostDto::fromPost);
  }

  public static PostDto fromPost(Post post) {
    return PostDto.builder()
        .postId(post.getId())
        .writerNickname(post.getMember().getNickname())
        .title(post.getTitle())
        .totalHits(post.getTotalHits())
        .totalLikes(post.getTotalLikes())
        .createdAt(post.getCreatedAt())
        .modifiedAt(post.getModifiedAt())
        .build();
  }
}
