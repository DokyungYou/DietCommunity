package com.example.dietcommunity.common.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisEmailService {

  private final RedisTemplate<String, String> redisAuthEmailTemplate;


  public void saveAuthEmailCode(String email, String authCode) {
    redisAuthEmailTemplate.opsForValue().set(email, authCode, Duration.ofMinutes(10));
  }


}