package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto login(LoginRequest dto) {
    User user = userRepository.findByUsernameAndPassword(dto.username(), dto.password())
        .orElseThrow(() -> new DiscodeitException(ErrorCode.INVALID_CREDENTIALS));
    if (user.getUserStatus() == null) {
      throw new DiscodeitException(ErrorCode.USER_STATUS_NOT_FOUND);
    }
    user.getUserStatus().update(Instant.now());//
    return userMapper.toDto(user);
  }
}
