package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.entity.Post;
import com.example.dietcommunity.post.model.PostDto;
import com.example.dietcommunity.post.type.PostSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
  Page<Post> getPostListGeneral(Long categoryId, PostSortType postSortType, Pageable pageable);

}
