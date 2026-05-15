package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.auth.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.auth.CustomAuthenticationEntryPoint;
import com.sprint.mission.discodeit.auth.LoginFailureHandler;
import com.sprint.mission.discodeit.auth.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //Set-Cookie 헤더 설정
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())// CSR 방식을 대비한 쿠키 해석 핸들러 커스텀
            .ignoringRequestMatchers("/h2-console/**") // h2 콘솔에 대한 csrf 설정 비활성화
        )
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin) // 같은 오리진 내 프레임 허용
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/csrf-token", "api/auth/login")
            .permitAll() // 로그인 및 csrf 토큰 발급 허용
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // 회원 가입 허용
            .requestMatchers("/api/**").authenticated() // 그외 모든 api 요청 인증 필요
            .anyRequest().permitAll() // api 요청이 아닌 다른 요청 허용
        )
        .formLogin(login -> login // form 로그인 활성화
            .loginProcessingUrl("/api/auth/login") // 로그인 url 변경
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

