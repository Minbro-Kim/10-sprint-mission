package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.NotAllowedInPrivateChannelException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;//공개채널에 멤버추가를 위한 의존성
  private final ChannelMapper channelMapper;

  @Override
  public ChannelDto create(PublicChannelCreateRequest dto) {
    log.debug("공개 채널 생성 시도: channelName={}", dto.name());
    Channel channel = channelMapper.toEntity(dto);
    channelRepository.save(channel);
    //모든 사용자가 멤버!
    userRepository.findAll()//한번에 저장하는방법?
        .forEach(m -> readStatusRepository.save(ReadStatus.create(m, channel, Instant.EPOCH)));
    log.info("공개 채널 생성 성공: channelId={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto create(PrivateChannelCreateRequest dto) {
    log.debug("비공개 채널 생성 시도: channelMembers={}", dto.memberIds());
    Channel channel = channelMapper.toEntity(dto);
    channelRepository.save(channel);
    List<User> members = userRepository.findAllById(dto.memberIds());//쿼리 한번으로 조회
    if (members.size() != dto.memberIds().size()) {//멤버가 전부 유저가 아닐때만
      log.warn("존재하지 않는 사용자ID에 대한 비공개 채널 생성 시도");
      throw new UserNotFoundException().addDetail("requestMemberIds", dto.memberIds())
          .addDetail("validMemberSize", members.size());
    }
    members
        .forEach(m -> {
          readStatusRepository.save(ReadStatus.create(m, channel, Instant.EPOCH));
        });
    log.info("비공개 채널 생성 성공: channelId={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    Channel channel = get(channelId);
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<ChannelDto> response = new ArrayList<>();
        /*
            사용자가 속한 채널 = 비공개+공개
         */
    //기존로직 - n+1 발생
//    readStatusRepository.findAllByUserId(userId)
//        .forEach(r -> {
//          response.add(channelMapper.toDto(r.getChannel()));
//        });
    //변경로직
    Map<UUID, List<User>> userMap = new HashMap<>();
    Map<UUID, Channel> myChannels = new HashMap<>();
    readStatusRepository.findAllByUserIdFetchChannel(userId)
        .forEach(r -> {
          userMap.putIfAbsent(r.getChannel().getId(), new ArrayList<>());
          myChannels.put(r.getChannel().getId(), r.getChannel());
        });
    readStatusRepository.findAllByChannelIdInFetchUser(userMap.keySet())
        .forEach(r -> {
          userMap.get(r.getChannel().getId()).add(r.getUser());
        });

    Map<UUID, Instant> lastMessages = new HashMap<>();
    messageRepository.findAllLastMessagesByChannelId(userMap.keySet())
        .forEach(m -> {
          lastMessages.put(m.getChannelId(), m.getMaxCreatedAt());
        });

    myChannels.values().forEach(c -> {
      response.add(channelMapper.toDto(c, userMap.get(c.getId()),
          lastMessages.getOrDefault(c.getId(), null)));
    });

    return response.stream()
        .sorted(Comparator.comparing(ChannelDto::createdAt))
        .toList();//채널 순서 보장
  }

  @Override
  public ChannelDto update(UUID id, PublicChannelUpdateRequest dto) {
    log.debug("채널 수정 시도: channelId={}", id);
    Channel channel = get(id);
    if (channel.getType() == ChannelType.PRIVATE) {
      log.warn("비공개 채널 수정 시도: channelId={}", channel.getId());
      throw new NotAllowedInPrivateChannelException().addDetail("channelId", id);
    }
    channel.update(dto.name(), dto.description());
    log.info("공개 채널 수정 성공: channelId={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Override
  public void delete(UUID channelId) {
    log.debug("채널 삭제 시도: channelId={}", channelId);
    if (!channelRepository.existsById(channelId)) {
      log.warn("존재하지 않는 채널 삭제 시도: channelId={}", channelId);
      throw new ChannelNotFoundException().addDetail("channelId", channelId);
    }
    messageRepository.deleteByChannelId(channelId);//메세지 먼저 삭제해야 바이너리 전부 삭제됨.배치 삭제 필요
    channelRepository.deleteById(channelId);
    //readStatusRepository.deleteByChannelId(channelId);데베 설정으로 자동 삭제
    log.info("채널 삭제 성공: channelId={}", channelId);
  }


  private Channel get(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException().addDetail("channelId", channelId));
  }
}
