package com.example.dietcommunity.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

  private static final long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60; // 1시간 밀리초
  private static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 일주일



  @Value("${spring.jwt.secretKey}")
  private String secretKey;

  private Key signitureKey;


  @PostConstruct
  protected void init() {
    signitureKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));     // WeakKeyException 발생가능
  }


  public String createToken(String email, long tokenValidTime) {

    Claims claims = Jwts.claims().setSubject(email);
    Date now = new Date();

    return Jwts.builder()
//        .setHeader()
        .setClaims(claims)
        .setIssuedAt(now) // 발행
        .setExpiration(new Date(now.getTime() + tokenValidTime)) // 유효기한
        .signWith(signitureKey, SignatureAlgorithm.HS256)
        .compact();
  }


  public String createAccessToken(String email) {
    return createToken(email, ACCESS_TOKEN_VALID_TIME);
  }
  public String createRefreshToken(String email) {
    return createToken(email, REFRESH_TOKEN_VALID_TIME);
  }



}
