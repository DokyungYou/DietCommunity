package com.example.dietcommunity.post.service;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.PostException;
import com.example.dietcommunity.common.s3.S3ImageService;
import com.example.dietcommunity.common.security.MemberDetails;
import com.example.dietcommunity.post.entity.Category;
import com.example.dietcommunity.post.entity.Post;
import com.example.dietcommunity.post.entity.PostImage;
import com.example.dietcommunity.post.model.CreatePostDto;
import com.example.dietcommunity.post.repository.CategoryRepository;
import com.example.dietcommunity.post.repository.PostImageRepository;
import com.example.dietcommunity.post.repository.PostRepository;
import com.example.dietcommunity.post.type.CategoryType;
import com.example.dietcommunity.post.type.PostStatus;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final PostImageRepository postImageRepository;
  private final S3ImageService s3ImageService;


  @Transactional
  public CreatePostDto.Response createPost(MemberDetails memberDetails,
      CreatePostDto.Request request, List<MultipartFile> images) {

    // 존재하는 카테고리인지 검증
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new PostException(ErrorCode.NON_EXISTENT_CATEGORY));

    // 일반게시글용 카테고리인지 검사
    if (category.getCategoryType() != CategoryType.GENERAL) {
      throw new PostException(ErrorCode.INVALID_CATEGORY_REQUEST);
    }

    // 게시글 저장
    Post savedPost = postRepository.save(
        Post.builder()
            .category(category)
            .member(memberDetails.toMember())
            .title(request.getTitle())
            .contents(request.getContents())
            .postStatus(PostStatus.NORMALITY)
            .totalHits(0)
            .totalLikes(0)
            .build());

    
    // 위의 조건을 다 만족하면 s3에 이미지 저장하고 db에 저장할 이미지url를 받아온 후 db에 저장
    List<PostImage> postImages = postImageRepository.saveAll(
        s3ImageService.upload(images)
            .stream()
            .map(imageUrl -> PostImage.builder()
                .post(savedPost)
                .imageUrl(imageUrl)
                .build())
            .collect(Collectors.toList()));

    return CreatePostDto.Response.of(savedPost, postImages);
  }
}
