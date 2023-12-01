package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.model.ChallengeDto;
import com.example.dietcommunity.post.type.ChallengeQueryFilter;
import com.example.dietcommunity.post.type.PostSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChallengeRepositoryCustom {

  Page<ChallengeDto> getChallengeList(Long categoryId, ChallengeQueryFilter status, PostSortType postSortType, Pageable pageable);


}
