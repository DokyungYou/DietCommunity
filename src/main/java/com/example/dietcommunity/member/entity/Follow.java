package com.example.dietcommunity.member.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Follow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long followId;

  @JoinColumn(name = "member_id")
  @ManyToOne
  private Member member;

  @JoinColumn(name = "following_id")
  @ManyToOne
  private Member following;

  @CreatedDate
  private LocalDateTime followedAt;
}
