package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.entity.Category;
import com.example.dietcommunity.post.model.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
  Page<PostDto> getPostListGeneral(Long categoryId, Pageable pageable);

}
