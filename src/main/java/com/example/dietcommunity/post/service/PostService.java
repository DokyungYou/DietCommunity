package com.example.dietcommunity.post.service;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.PostException;
import com.example.dietcommunity.common.s3.S3ImageService;
import com.example.dietcommunity.common.security.MemberDetails;
import com.example.dietcommunity.post.entity.Category;
import com.example.dietcommunity.post.entity.Post;
import com.example.dietcommunity.post.entity.PostImage;
import com.example.dietcommunity.post.model.PostWriteDto;
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
  public PostWriteDto.Response createPost(MemberDetails memberDetails,
      PostWriteDto.Request request, List<MultipartFile> images) {

    // 요청에 부합하는 카테고리인지 확인
    Category category = categoryRepository.findByIdAndAndCategoryType(request.getCategoryId(), CategoryType.GENERAL)
        .orElseThrow(() -> new PostException(ErrorCode.INVALID_CATEGORY_REQUEST));


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


    List<PostImage> postImages = savePostImages(images, savedPost);

    return PostWriteDto.Response.of(savedPost, postImages);
  }


  @Transactional
  public PostWriteDto.Response updatePost(MemberDetails memberDetails, long postId,
      PostWriteDto.Request request, List<MultipartFile> images) {

    // 요청에 부합하는 카테고리인지 확인
    Category category = categoryRepository.findByIdAndAndCategoryType(request.getCategoryId(), CategoryType.GENERAL)
        .orElseThrow(() -> new PostException(ErrorCode.INVALID_CATEGORY_REQUEST));


    Post post = postRepository.findByIdAndPostStatusNot(postId, PostStatus.DELETED)
        .orElseThrow(() -> new PostException(ErrorCode.NOT_FOUND_POST));

    // 자신이 작성하지 않은 게시글은 수정불가
    if(!memberDetails.getMemberId().equals(post.getMember().getId())){
      throw new PostException(ErrorCode.CANNOT_EDIT_OTHERS_POST);
    }


    // post 업데이트
    post.updatePost(request, category);
    Post updatedPost = postRepository.save(post);


    // s3에 저장했었던 이미지 삭제 후 db에 있는 이미지 삭제
    List<PostImage> existingImages = postImageRepository.findByPost_Id(postId);

    s3ImageService.deleteAllImages(existingImages);
    postImageRepository.deleteAll(existingImages);


    // 새로운 이미지 저장
    List<PostImage> postImages = savePostImages(images, updatedPost);


    return PostWriteDto.Response.of(updatedPost, postImages);
  }

  private List<PostImage> savePostImages(List<MultipartFile> images, Post savedPost) {

    List<PostImage> postImages = postImageRepository.saveAll(
        s3ImageService.uploadImages(images, savedPost.getId()) // s3에 저장하고 생성된 url를 반환받음
            .stream()
            .map(imageUrl -> PostImage.builder()  // url 로 PostImage 엔티티 생성
                .post(savedPost)
                .imageUrl(imageUrl)
                .build())
            .collect(Collectors.toList()));
    return postImages;
  }

}
