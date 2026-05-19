package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @Override
  public UserDto updateRole(UserRoleUpdateRequest request) {
    log.debug("사용자 권한 변경: userId={}, newRole={}", request.userId(), request.newRole());
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException().addDetail("userId", request.userId()));

    UserDetails userDetails = new DiscodeitUserDetails(userMapper.toDto(user, false),
        user.getPassword());
    if (user.getRole() == request.newRole()) {
      log.info("사용자 권한 변경 요청(동일한 권한, 세션 만료x): userId={}", request.userId());
      return userMapper.toDto(user,
          !sessionRegistry.getAllSessions(userDetails,
              false).isEmpty());
    }
    user.updateRole(request.newRole());

    List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(userDetails,
        false);
    sessionInformations.forEach(SessionInformation::expireNow);
    log.info("사용자 권한 변경 및 세션 만료 완료: userId={}, newRole={}", request.userId(), request.newRole());
    return userMapper.toDto(user, false);
  }

}
