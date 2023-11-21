package com.example.dietcommunity.common.security;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.SecurityExceptionCustom;
import com.example.dietcommunity.member.entity.MemberAuthToken;
import com.example.dietcommunity.member.repository.MemberRepository;
import com.example.dietcommunity.member.repository.MemberTokenRedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

  public static final String TOKEN_HEADER = "Authorization";
//  private static final long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60; // 1시간 밀리초 (기본)
//  private static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 일주일 (기본)

  //테스트용
  private static final long ACCESS_TOKEN_VALID_TIME = 1000L * 60; // 1분
  private static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 2; // 2분


  private final MemberDetailService memberDetailService;
  private final MemberTokenRedisRepository memberTokenRedisRepository;
  private final MemberRepository memberRepository;


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


  public String getEmailFromToken(String token) {

    return Jwts.parserBuilder()
        .setSigningKey(signitureKey)  // 서명키
        .build() // JwtParser 로 생성
        .parseClaimsJws(token)  // 토큰을 파싱하고 검증하는 부분
        .getBody()  // 페이로드 부분 찾기
        .getSubject(); // 이메일 가져오기
  }


  public boolean validateToken(String token) {

    try {
      Jwts.parserBuilder()
          .setSigningKey(signitureKey)
          .build()
          .parseClaimsJws(token);
      return true;

    } catch (ExpiredJwtException e) {
      log.info("기한만료 토큰");
      return false;
    } catch (Exception e) {
      throw new SecurityExceptionCustom(ErrorCode.INVALID_TOKEN);
    }
  }


  public String resolveToken(HttpServletRequest request) {

    return request.getHeader(TOKEN_HEADER);  // 특정헤더로부터 토큰값을 가져옴 (accessToken)
  }


  // 토큰으로 인증 객체(Authentication) 얻기
  public Authentication getAuthentication(String token) {
    log.debug("[getAuthentication] 토큰 인증 정보 조회");
    UserDetails userDetails = memberDetailService.loadUserByUsername(
        getEmailFromToken(token));  // SecurityExceptionCustom
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }


  // AccessToken 재발급
  public String reIssue(MemberAuthToken memberAuthToken) {

    String newAccessToken = createAccessToken(getEmailFromToken(memberAuthToken.getRefreshToken()));

    memberAuthToken.setAccessToken(newAccessToken);
    memberTokenRedisRepository.save(memberAuthToken);

    return newAccessToken;
  }


}
