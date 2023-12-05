package com.example.dietcommunity.post.service;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.PostException;
import com.example.dietcommunity.common.s3.S3ImageService;
import com.example.dietcommunity.common.security.MemberDetails;
import com.example.dietcommunity.post.entity.Category;
import com.example.dietcommunity.post.entity.Challenge;
import com.example.dietcommunity.post.entity.Post;
import com.example.dietcommunity.post.entity.PostImage;
import com.example.dietcommunity.post.model.ChallengeDto;
import com.example.dietcommunity.post.model.ChallengeWriteDto;
import com.example.dietcommunity.post.model.PostDto;
import com.example.dietcommunity.post.model.PostWriteDto;
import com.example.dietcommunity.post.repository.CategoryRepository;
import com.example.dietcommunity.post.repository.ChallengeRepository;
import com.example.dietcommunity.post.repository.PostImageRepository;
import com.example.dietcommunity.post.repository.PostRepository;
import com.example.dietcommunity.post.type.CategoryType;
import com.example.dietcommunity.post.type.ChallengeQueryFilter;
import com.example.dietcommunity.post.type.PostSortType;
import com.example.dietcommunity.post.type.PostStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PostService {

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final PostImageRepository postImageRepository;
  private final ChallengeRepository challengeRepository;
  private final S3ImageService s3ImageService;


  @Transactional
  public PostWriteDto.Response createPost(MemberDetails memberDetails,
      PostWriteDto.Request request, List<MultipartFile> images) {

    // 요청에 부합하는 카테고리인지 확인
    Category category = categoryRepository.findByIdAndCategoryType(request.getCategoryId(), CategoryType.GENERAL)
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


    List<PostImage> savedImages = savePostImages(images, savedPost);

    return PostWriteDto.Response.of(savedPost, savedImages);
  }


  @Transactional
  public PostWriteDto.Response updatePost(MemberDetails memberDetails, long postId,
      PostWriteDto.Request request, List<MultipartFile> newImages) {

    Post post = postRepository.findByIdAndPostStatusNot(postId, PostStatus.DELETED)
        .orElseThrow(() -> new PostException(ErrorCode.NOT_FOUND_POST));

    // 자신이 작성하지 않은 게시글은 수정불가
    if(!memberDetails.getMemberId().equals(post.getMember().getId())){
      throw new PostException(ErrorCode.CANNOT_EDIT_OTHERS_POST);
    }

    // 요청에 부합하는 카테고리인지 확인
    Category category = categoryRepository.findByIdAndCategoryType(request.getCategoryId(), CategoryType.GENERAL)
        .orElseThrow(() -> new PostException(ErrorCode.INVALID_CATEGORY_REQUEST));


    // 게시글 업데이트
    Post updatedPost = postRepository.save(post.updatePost(request, category));

    // 이미지 업데이트
    List<PostImage> postImages = updateImages(newImages, updatedPost);


    return PostWriteDto.Response.of(updatedPost, postImages);
  }



  private List<PostImage> savePostImages(List<MultipartFile> images, Post savedPost) {

    if(images == null || images.isEmpty()){
      return new ArrayList<>();
    }
    if (images.size() > 5) {
      throw new IllegalArgumentException("이미지는 5개까지 저장가능합니다.");
    }


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


  private List<PostImage> updateImages(List<MultipartFile> newImages, Post post){

    List<PostImage> existingImages = postImageRepository.findByPost_Id(post.getId());
    s3ImageService.deleteAllImages(existingImages);
    postImageRepository.deleteAll(existingImages);

    // 새로운 이미지로 저장
    return savePostImages(newImages, post);
  }

  @Transactional
  public ChallengeWriteDto.Response createPostChallenge(MemberDetails memberDetails,
      ChallengeWriteDto.Request request, List<MultipartFile> images) {

    PostWriteDto.Request postRequest = request.getPostRequest();

    // 요청에 부합하는 카테고리인지 확인
    Category category = categoryRepository.findByIdAndCategoryType(postRequest.getCategoryId(), CategoryType.CHALLENGE)
        .orElseThrow(() -> new PostException(ErrorCode.INVALID_CATEGORY_REQUEST));

    // 챌린지게시글의 제목, 내용, 이미지 저장
    Post savedPost = postRepository.save(
        Post.builder()
            .category(category)
            .member(memberDetails.toMember())
            .title(postRequest.getTitle())
            .contents(postRequest.getContents())
            .postStatus(PostStatus.NORMALITY)
            .totalHits(0)
            .totalLikes(0)
            .build());

    List<PostImage> savedImages = savePostImages(images, savedPost);


    // 챌린지 채널로 사용할 카테고리 생성
    Category challengeChannel = categoryRepository.save(Category.builder()
        .categoryType(CategoryType.CHALLENGE_CHANNEL)
        .categoryName(postRequest.getTitle())
        .build());


    // 챌린지에 대한 내용 저장
    Challenge savedChallenge = challengeRepository.save(Challenge.builder()
        .post(savedPost)
        .challengeStartDate(request.getStartDate())
        .challengeEndDate(request.getEndDate())
        .limitApplicantsNumber(request.getLimitApplicantsNumber())
        .participateChannelId(challengeChannel.getId())
        .build());

    return ChallengeWriteDto.Response.of(savedChallenge, savedImages);

  }


  // Todo: 해당 챌린지에 참여신청한 회원이 존재 시 신청인원제한수를 줄일 수 없음 조건 추가
  @Transactional
  public ChallengeWriteDto.Response updatePostChallenge(MemberDetails memberDetails, long challengeId,
      ChallengeWriteDto.Request request, List<MultipartFile> newImages) {
    
    // 존재하는 챌린지인지 확인
    Challenge challenge = challengeRepository.findById(challengeId)
        .orElseThrow(() -> new PostException(ErrorCode.NOT_FOUND_POST));

    // 자신이 작성한 챌린지인지 확인
    Post post = challenge.getPost();
    if(!post.getMember().getId().equals(memberDetails.getMemberId())){
      throw new PostException(ErrorCode.CANNOT_EDIT_OTHERS_POST);
    }

    // 기존의 챌린지 시작날짜가 이미 지났다면 수정불가
    if(challenge.getChallengeStartDate().isBefore(LocalDate.now())){
      throw new PostException(ErrorCode.CANNOT_EDIT_CHALLENGE_PASSED_START_DATE);
    }

    
    // 요청에 부합하는 카테고리인지 확인
    Category category = categoryRepository.findByIdAndCategoryType(request.getPostRequest().getCategoryId(), CategoryType.CHALLENGE)
        .orElseThrow(() -> new PostException(ErrorCode.INVALID_CATEGORY_REQUEST));


    // 챌린지채널용 카테고리의 이름 수정
    Category updatedCategory = categoryRepository.save(category.updateName(request.getPostRequest().getTitle()));

    // 챌린지게시글내용, 이미지 수정
    Post updatedPost = postRepository.save(post.updatePost(request.getPostRequest(), updatedCategory));
    List<PostImage> updatedImages = updateImages(newImages, updatedPost);

    Challenge updatedChallenge = challengeRepository.save(challenge.updateChallenge(request, updatedPost));

    return ChallengeWriteDto.Response.of(updatedChallenge, updatedImages);

  }


  public Page<PostDto> getPostListGeneral(Long categoryId, PostSortType postSortType,Pageable pageable) {

    // 일반게시글타입 카테고리인지 확인해야함
    if(categoryId != null && !categoryRepository.existsByIdAndCategoryType(categoryId, CategoryType.GENERAL)){
        throw new PostException(ErrorCode.INVALID_CATEGORY_REQUEST);
    }

    return PostDto.toDtoPage(postRepository.getPostListGeneral(categoryId, postSortType, pageable));
  }

  public Page<ChallengeDto> getChallengeListGeneral(
      Long categoryId, ChallengeQueryFilter status, PostSortType postSortType, Pageable pageable) {

    // 챌린지타입 카테고리인지 확인
    if (categoryId != null && !categoryRepository.existsByIdAndCategoryType(categoryId, CategoryType.CHALLENGE)) {
      throw new PostException(ErrorCode.INVALID_CATEGORY_REQUEST);
    }

    return ChallengeDto.toDtoPage(challengeRepository.getChallengeList(categoryId, status, postSortType, pageable));
  }


  @Transactional
  public void removePost(MemberDetails memberDetails, long postId) {// 일반사용자가 이용하는 삭제기능 (실제 데이터를 바로 지우지는 않음)

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.NOT_FOUND_POST));

    if (!memberDetails.getMemberId().equals(post.getMember().getId())) {
      throw new PostException(ErrorCode.CANNOT_EDIT_OTHERS_POST);
    }

    postRepository.save(post.removePost());
  }

  // Todo: 삭제할 수 없는 상황의 조건문 추가 -> 챌린지에 참여신청한 회원 존재 시 삭제불가 (참여신청기능 만들 때 추가하기)
  @Transactional
  public void removeChallenge(MemberDetails memberDetails, long challengeId) { // 일반사용자가 이용하는 삭제기능 (실제 데이터를 바로 지우지는 않음)

    Challenge challenge = challengeRepository.findById(challengeId)
        .orElseThrow(() -> new PostException(ErrorCode.NOT_FOUND_POST));

    Post post = challenge.getPost();

    if (!memberDetails.getMemberId().equals(post.getMember().getId())) {
      throw new PostException(ErrorCode.CANNOT_EDIT_OTHERS_POST);
    }

    postRepository.save(post.removePost());
  }
}
