package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatusDto create(ReadStatusCreateRequest dto) {
    User user = userRepository.findById(dto.userId())
        .orElseThrow(() -> new DiscodeitException(ErrorCode.USER_NOT_FOUND));
    Channel channel = channelRepository.findById(dto.channelId())
        .orElseThrow(() -> new DiscodeitException(ErrorCode.CHANNEL_NOT_FOUND));
    if (readStatusRepository.findByUserIdAndChannelId(dto.userId(), dto.channelId()).isPresent()) {
      throw new DiscodeitException(ErrorCode.READ_STATUS_ALREADY_EXIST);
    }
    ReadStatus readStatus = readStatusRepository.save(
        ReadStatus.create(user, channel, dto.lastReadAt()));
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatus find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new DiscodeitException(ErrorCode.READ_STATUS_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  public ReadStatusDto update(UUID id, ReadStatusUpdateRequest dto) {
    ReadStatus status = find(id);
    status.update(dto.lastReadAt());//마지막 시간 현재로 업데이트
    return readStatusMapper.toDto(status);
  }

  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new DiscodeitException(ErrorCode.READ_STATUS_NOT_FOUND);
    }
    readStatusRepository.deleteById(readStatusId);
  }

  private void checkValidation(UUID userId, UUID channelId) {
    if (userRepository.findById(userId).isEmpty()) {
      throw new DiscodeitException(ErrorCode.USER_NOT_FOUND);
    }
    if (channelRepository.findById(channelId).isEmpty()) {
      throw new DiscodeitException(ErrorCode.CHANNEL_NOT_FOUND);
    }
    if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
      throw new DiscodeitException(ErrorCode.READ_STATUS_ALREADY_EXIST);
    }
  }
}
