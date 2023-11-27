package com.example.dietcommunity.common.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 현재는 특정권한의 기능 구현 전이라 틀만 만들어두었음
public class AccessDeniedHandlerCustom implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) //throws IOException, ServletException
  {
    log.error("[Access is Denied] 접근 불가");
  }
}
