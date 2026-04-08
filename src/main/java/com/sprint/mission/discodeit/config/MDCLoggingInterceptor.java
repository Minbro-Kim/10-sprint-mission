package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  //OncePerRequestFilter을 사용하는 것이 더 정석 -> 시큐리티보다 앞단에서 찍을 수 있음

  private static final String REQUEST_ID = "request_id";
  private static final String REQUEST_METHOD = "request_method";
  private static final String REQUEST_URL = "request_url";
  private static final String HEADER_REQUEST_ID = "Discodeit-Request-ID";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    // 랜덤 생성
    String requestId = UUID.randomUUID().toString().substring(0, 8);//8자리까지만

    // MDC에 정보 담기
    MDC.put(REQUEST_ID, requestId);
    MDC.put(REQUEST_METHOD, request.getMethod());
    MDC.put(REQUEST_URL, request.getRequestURI());

    // 응답 헤더에 포함
    response.setHeader(HEADER_REQUEST_ID, requestId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    // 요청 끝나고 MDC 비우기(스레드 풀에 반납 전에 비우기)
    MDC.clear();
  }

}
