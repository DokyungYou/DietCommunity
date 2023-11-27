package com.example.dietcommunity.common.security;

import com.example.dietcommunity.common.exception.SecurityExceptionCustom;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointCustom implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;


  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException {

    log.error("[AuthenticationEntryPointCustom : commence] 인증 실패로 response.sendError 발생.");
    Object exceptionAttribute = request.getAttribute("exception"); // 필터에서 넘긴 값

    if (exceptionAttribute != null) {

      if(exceptionAttribute instanceof  SecurityExceptionCustom){
        SecurityExceptionCustom exception = (SecurityExceptionCustom) exceptionAttribute;
        log.error("[SecurityExceptionCustom] errorCode: {}, message: {}", exception.getErrorCode(), exception.getMessage());
      }else {
        Exception exception = (Exception) exceptionAttribute;
        log.error("[Exception] exceptionName: {}, message: {}", exception.getClass().getName() ,exception.getMessage());
      }


      // 클라이언트에는 인증이 실패했다는 정보만 노출
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401에러
      response.setContentType("application/json");
      response.setCharacterEncoding("utf-8");
      response.getWriter()
          .write(objectMapper.writeValueAsString("인증이 실패하였습니다.")); // Spring 을 거치는 것이 아니기때문에 한글 처리를 위해 필요한 부분

    }


  }
}
