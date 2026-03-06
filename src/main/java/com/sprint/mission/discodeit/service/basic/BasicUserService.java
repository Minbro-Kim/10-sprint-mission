package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  //모든 멤버를 공개채널에 추가하기 위해..
  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public UserDto create(UserCreateRequest dto,
      Optional<BinaryContentCreateDto> binaryContentCreateDto) {
    validateEmail(dto.email());
    validateUsername(dto.username());
    //프로필 사진
    BinaryContent profile = null;
    if (binaryContentCreateDto.isPresent()) {
      profile = binaryContentMapper.toEntity(binaryContentCreateDto.get());
    }
    User user = userMapper.toEntity(dto, profile);
    UserStatus userStatus = UserStatus.create(user, Instant.now());
    //모든 공개채널에 대한 읽기 상태 저장
    userRepository.save(user);
    channelRepository.findAllPublic()
        .forEach(c -> readStatusRepository.save(ReadStatus.create(user, c, Instant.EPOCH)));
    if (binaryContentCreateDto.isPresent()) {
      binaryContentStorage.put(profile.getId(), binaryContentCreateDto.get().bytes());
    }
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    User user = get(userId);
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAllFetchUserInfo();
    List<UserDto> response = new ArrayList<>();
    users.forEach(u -> response.add(userMapper.toDto(u)));
    return response;
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest dto,
      Optional<BinaryContentCreateDto> binaryContentCreateDto) {
    User user = get(userId);
    BinaryContent profile = null;
    if (binaryContentCreateDto.isPresent()) {
      profile = binaryContentMapper.toEntity(binaryContentCreateDto.get());
      binaryContentRepository.save(profile);//profile id가 필요하기 때문에
    }
    user.update(dto.username(), dto.email(), dto.password(), profile);
    if (binaryContentCreateDto.isPresent()) {//위에서 생성하면 업데이트 실패시 스토리지 저장을 되돌릴수 없기 때문
      binaryContentStorage.put(profile.getId(), binaryContentCreateDto.get().bytes());
    }
    return userMapper.toDto(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = get(userId);
//    if (user.getProfile() != null) {//프로필 사진 있는경우 삭제
//      binaryContentRepository.deleteById(user.getProfile().getId());
//    }
    readStatusRepository.deleteByUserId(userId);//삭제된 사용자를 공개채널 멤버나 프라이빗 채널 멤버에서 제거
    userRepository.deleteById(userId);
  }

  private void validateEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXIST);
    }
  }

  private void validateUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      throw new BusinessLogicException(ExceptionCode.USER_NAME_ALREADY_EXIST);
    }
  }

  private UserStatus findUserStatusByUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
  }

  private User get(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
  }
}
