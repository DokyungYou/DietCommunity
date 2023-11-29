package com.example.dietcommunity.post.controller;


import com.example.dietcommunity.common.security.MemberDetails;
import com.example.dietcommunity.post.model.ChallengeWriteDto;
import com.example.dietcommunity.post.model.PostWriteDto;
import com.example.dietcommunity.post.service.PostService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;


  @PostMapping
  public ResponseEntity<PostWriteDto.Response> createPostGeneral(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @Valid @RequestPart PostWriteDto.Request request,
      @RequestPart(value = "images", required = false) List<MultipartFile> images){

    return ResponseEntity.ok(postService.createPost(memberDetails, request, images));

  }


  @PutMapping("/{postId}")
  public ResponseEntity<PostWriteDto.Response> updatePostGeneral(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable long postId,
      @Valid @RequestPart PostWriteDto.Request request,
      @RequestPart(value = "images", required = false) List<MultipartFile> images){

    return ResponseEntity.ok( postService.updatePost(memberDetails, postId, request, images));
  }


  @PostMapping("/challenge")
  public ResponseEntity<ChallengeWriteDto.Response> createPostChallenge(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @Valid @RequestPart ChallengeWriteDto.Request request,
      @RequestPart(value = "images", required = false) List<MultipartFile> images){

    request.validateInputDate();
    return ResponseEntity.ok( postService.createPostChallenge(memberDetails, request, images));
  }


  @PutMapping("/challenge/{challengeId}")
  public ResponseEntity<ChallengeWriteDto.Response> updatePostChallenge(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable long challengeId,
      @Valid @RequestPart ChallengeWriteDto.Request request,
      @RequestPart(value = "images", required = false) List<MultipartFile> images){

    request.validateInputDate();
    return ResponseEntity.ok( postService.updatePostChallenge(memberDetails, challengeId, request, images));
  }



}
