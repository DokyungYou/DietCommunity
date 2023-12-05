package com.example.dietcommunity.post.entity;

import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.post.model.PostWriteDto;
import com.example.dietcommunity.post.type.PostStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  private Category category;

  @ManyToOne
  private Member member;

  private String title;

  @Column(length = 500)
  private String contents;

  private long totalHits;

  private long totalLikes;

  @Enumerated(value = EnumType.STRING)
  private PostStatus postStatus;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime modifiedAt;


  public Post updatePost(PostWriteDto.Request request, Category category){
    this.category = category;
    this.title = request.getTitle();
    this.contents = request.getContents();

    return this;
  }

  public Post removePost(){
    this.postStatus = PostStatus.DELETED;
    return this;
  }
}
