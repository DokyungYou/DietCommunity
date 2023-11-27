package com.example.dietcommunity.member.repository;

import com.example.dietcommunity.member.entity.MemberAuthToken;
import java.util.Optional;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableRedisRepositories
public interface MemberTokenRedisRepository extends CrudRepository<MemberAuthToken, Long> {
  Optional<MemberAuthToken> findByAccessToken(String accessToken);
}
