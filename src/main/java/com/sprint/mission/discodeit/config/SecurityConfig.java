package com.sprint.mission.discodeit.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //Set-Cookie 헤더 설정
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())// CSR 방식을 대비한 쿠키 해석 핸들러 커스텀
            .ignoringRequestMatchers(PathRequest.toH2Console()) // h2 콘솔에 대한 csrf 설정 비활성화
        )
        .headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin()) // 같은 도메인 내 프레임 허용
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll() // 로그인 및 csrf 토큰 발급 허용
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // 회원 가입 허용
            .requestMatchers("/api/**").authenticated() // 그외 모든 api 요청 인증 필요
            .anyRequest().permitAll() // api 요청이 아닌 다른 요청 허용
        )
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

