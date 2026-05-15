package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "admin.enable", havingValue = "true")
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final UserService userService;

  @Value("${admin.email:admin@admin.com}")
  private String email;
  @Value("${admin.username:admin}")
  private String username;
  @Value("${admin.password:admin1234!}")
  private String password;

  @Override
  public void run(String... args) throws Exception {
    if (userRepository.existsByRole(Role.ADMIN)) {
      log.info("✅ 관리자 계정이 존재합니다. 관리자 계정 생성을 건너뜁니다.");
      return;
    }
    Optional<BinaryContentCreateDto> profile = Optional.empty();
    UserCreateRequest request = new UserCreateRequest(username, email, password);

    try {
      userService.create(request, profile);
      log.info("✅ 관리자 계정이 생성되었습니다. username={}", username);
    } catch (Exception e) {
      log.error("⛔️ 관리자 계정 생성 실패: ", e);
      log.error("⛔️관리자 계정 생성 실패로 프로그램을 종료합니다.");
      throw e;
    }

  }
}
