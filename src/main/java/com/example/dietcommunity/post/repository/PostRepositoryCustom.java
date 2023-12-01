package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.model.PostDto;
import com.example.dietcommunity.post.type.PostSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
  Page<PostDto> getPostListGeneral(Long categoryId, PostSortType postSortType, Pageable pageable);

}
