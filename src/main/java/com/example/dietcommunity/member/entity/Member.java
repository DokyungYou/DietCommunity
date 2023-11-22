package com.example.dietcommunity.member.entity;

import com.example.dietcommunity.member.type.MemberRole;
import com.example.dietcommunity.member.type.MemberStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String accountId;

  @Column(unique = true)
  private String nickname;

  private String password;

  private String selfIntroduction;

  private Integer finalGoalWeight;


  @Enumerated(EnumType.STRING)
  private MemberStatus status;


  @Enumerated(EnumType.STRING)
  private MemberRole role;


  @CreatedDate
  private LocalDateTime registeredAt;

  @LastModifiedDate
  private LocalDateTime modifiedAt;


  public void activeMemberStatus(){
    this.status = MemberStatus.ACTIVATED;
  }

  public void updatePassword(String encodedPassword){
    this.password = encodedPassword;
  }

  public void withdrawMember(){
    this.status = MemberStatus.WITHDRAWN;
  }

}
