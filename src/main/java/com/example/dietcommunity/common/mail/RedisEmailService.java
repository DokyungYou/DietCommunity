package com.example.dietcommunity.common.mail;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.InvalidAuthCodeException;
import com.example.dietcommunity.common.exception.MemberException;
import com.example.dietcommunity.member.entity.Member;
import com.example.dietcommunity.member.repository.MemberRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RedisEmailService {

  private final RedisTemplate<String, String> redisAuthEmailTemplate;
  private final MemberRepository memberRepository;


  public void saveAuthEmailCode(String email, String authCode) {
    redisAuthEmailTemplate.opsForValue().set(email, authCode, Duration.ofMinutes(10));
  }

  public void deleteAuthEmail(String email){
    redisAuthEmailTemplate.delete(email);
  }


  @Transactional
  public void validateAuthEmailCode(String email, String authCode) {
    String savedAuthCode = redisAuthEmailTemplate.opsForValue().get(email);
    if (!authCode.equals(savedAuthCode)) {
      throw new InvalidAuthCodeException(ErrorCode.INVALID_AUTH_CODE);
    }

    // 계정을 활성화
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    member.activeMemberStatus();
    memberRepository.save(member);
  }
}