package com.example.dietcommunity.post.model;

import com.example.dietcommunity.post.entity.Post;
import com.example.dietcommunity.post.entity.PostImage;
import com.example.dietcommunity.post.type.PostStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
public class PostWriteDto {


  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    @NotNull(message = "카테고리를 선택해주세요.")
    private long categoryId;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String contents;


  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private long postId;
    private String title;
    private String contents;
    private PostStatus postStatus;
    private LocalDateTime registeredAt;
    private List<String> imageUrls;

    public static Response of(Post post, List<PostImage> images) {

      List<String> imageUrls = images.stream()
          .map(PostImage::getImageUrl)
          .collect(Collectors.toList());


      return Response.builder()
          .postId(post.getId())
          .title(post.getTitle())
          .contents(post.getContents())
          .postStatus(post.getPostStatus())
          .registeredAt(post.getRegisteredAt())
          .imageUrls(imageUrls)
          .build();
    }
  }
}


