package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.entity.Post;
import com.example.dietcommunity.post.type.PostStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> , PostRepositoryCustom{

  Optional<Post> findByIdAndPostStatusNot(long postId, PostStatus status);
}
