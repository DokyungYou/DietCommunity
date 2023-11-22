package com.example.dietcommunity.member.model.response;

import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.type.MemberStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WithdrawResponse {

  private long memberId;
  private MemberStatus memberStatus;
  private LocalDateTime withdrawAt;

  public WithdrawResponse(Member member){
    this.memberId = member.getMemberId();
    this.memberStatus = member.getStatus();
    this.withdrawAt = member.getModifiedAt();
  }
}
