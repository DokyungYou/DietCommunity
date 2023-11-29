package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.entity.PostImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

  List<PostImage> findByPost_Id(long postId);
}
