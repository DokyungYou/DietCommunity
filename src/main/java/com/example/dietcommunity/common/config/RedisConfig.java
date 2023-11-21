package com.example.dietcommunity.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  /* <현재계획>
   Redis1: 인증토큰 -(0) | 인증코드 -(1) | 채팅전송에 사용될 회원정보(닉네임 등) -(확정x)
   Redis2: 그 외 -(미정)
   */

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  public RedisConnectionFactory createLettuceConnectionFactory(int dbIndex) {
    final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(redisHost);
    redisStandaloneConfiguration.setPort(redisPort);
    redisStandaloneConfiguration.setDatabase(dbIndex);

    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }


  // 데이터베이스 분리해서 관리
  @Bean
  @Primary
  public RedisConnectionFactory redisConnectionFactory() {
    return createLettuceConnectionFactory(0); 
  }
  @Bean
  public RedisConnectionFactory redisAuthEmailConnectionFactory() {
    return createLettuceConnectionFactory(1);
  }


  @Bean
  public RedisTemplate<String, String> redisAuthEmailTemplate() {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisAuthEmailConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());

    return redisTemplate;
  }

}
