package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatus create(UserStatusCreateDto dto) {
    User user = userRepository.findById(dto.userId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    if (userStatusRepository.existsByUserId(dto.userId())) {
      throw new BusinessLogicException(ExceptionCode.USER_STATUS_ALREADY_EXIST);
    }
    return userStatusRepository.save(UserStatus.create(user, Instant.now()));
  }

  @Override
  public UserStatus update(UUID id, UserStatusUpdateDto dto) {
    UserStatus status = find(id);
    status.update(dto.lastActiveAt());
    //return userStatusRepository.save(status);
    return status;
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateDto dto) {
//    User user = userRepository.findById(userId)
//        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    UserStatus status = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    status.update(dto.lastActiveAt());
    return userStatusMapper.toDto(status);
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatus find(UUID id) {
    return userStatusRepository.findById(id)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public void delete(UUID id) {
    if (!userStatusRepository.existsById(id)) {
      throw new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND);
    }
    userStatusRepository.deleteById(id);
  }
}
