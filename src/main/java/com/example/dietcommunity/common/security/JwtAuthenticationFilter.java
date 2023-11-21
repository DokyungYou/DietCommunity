package com.example.dietcommunity.common.security;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.common.exception.SecurityExceptionCustom;
import com.example.dietcommunity.member.entity.MemberAuthToken;
import com.example.dietcommunity.member.repository.MemberTokenRedisRepository;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";
  private final JwtTokenProvider jwtTokenProvider;
  private final MemberTokenRedisRepository memberTokenRedisRepository;



  // 테스트 결과 필터 안에서 터진 예외는 핸들러에서 해결이 안되는 듯하다.
  // 전체를 try catch로 잡아준 뒤 넘긴 값을 AuthenticationEntryPointCustom 에서 처리하는 방식으로 하였음
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // http 요청의 헤더에서 토큰 추출
    log.info("[doFilterInternal] resolve Token");
    String accessToken = jwtTokenProvider.resolveToken(request);

    // 토큰이 필요하지 않은 api일 경우 로직 처리없이 다음 필터로 이동
    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {

      // AccessToken의 유효성 검증 실패 (기한만료)
      if (!jwtTokenProvider.validateToken(accessToken)) {

        // redis에서 저장했던 토큰set을 가져올 수 있음
        MemberAuthToken memberAuthToken = memberTokenRedisRepository.findByAccessToken(accessToken)
            .orElseThrow(() -> new SecurityExceptionCustom(ErrorCode.NOT_FOUND_TOKEN_SET));

        // RefreshToken도 기한만료 시 -> 재로그인하여 토큰Set 을 새로 발급받아야함
        if (!jwtTokenProvider.validateToken(memberAuthToken.getRefreshToken())) {
          throw new SecurityExceptionCustom(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // RefreshToken 유효한 경우 AccessToken 재발급
        // 재발급한 AccessToken 을 꺼냈던 RefreshToken 과 함께 저장
        // 재발급한 AccessToken 로 다시 넘김
        accessToken = jwtTokenProvider.reIssue(memberAuthToken);
        response.setHeader(TOKEN_HEADER, accessToken);

      }

      // 유효한 AccessToken 으로 authentication 을 생성하여 SecurityContextHolder 에 넣어준다.
      Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception e) {
      request.setAttribute("exception", e); // filter 내에서 발생한 예외를 넘겨 AuthenticationEntryPointCustom 에서 처리
    }

    filterChain.doFilter(request, response);
  }
}
