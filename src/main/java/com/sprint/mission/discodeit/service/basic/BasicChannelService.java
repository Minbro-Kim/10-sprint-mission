package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.comparator.Comparators;

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
    //System.out.println("채널 생성 시작!!!");
    Channel channel = channelMapper.toEntity(dto);
    //System.out.println("엔티티 변환 끝");
    channelRepository.save(channel);
    //System.out.println("채널 저장 완료");
    //모든 사용자가 멤버!
    userRepository.findAll()//한번에 저장하는방법?
        .forEach(m -> readStatusRepository.save(ReadStatus.create(m, channel, Instant.EPOCH)));
    //System.out.println("읽음 상태 저장 완료");
    System.out.println("채널 생성 읽음 상태 초기값: " + Instant.EPOCH);
    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto create(PrivateChannelCreateRequest dto) {
    Channel channel = channelMapper.toEntity(dto);
    channelRepository.save(channel);
    List<User> members = userRepository.findAllById(dto.memberIds());//쿼리 한번으로 조회
    if (members.size() != dto.memberIds().size()) {
      throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }
    members
        .forEach(m -> {
          readStatusRepository.save(ReadStatus.create(m, channel, Instant.EPOCH));
        });
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
    Channel channel = get(id);
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new BusinessLogicException(ExceptionCode.NOT_ALLOWED_IN_PRIVATE_CHANNEL);
    }
    channel.update(dto.name(), dto.description());
    return channelMapper.toDto(channel);
  }

  @Override
  public void delete(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND);
    }
    messageRepository.deleteByChannelId(channelId);//메세지 먼저 삭제해야 바이너리 전부 삭제됨.배치 삭제 필요
    channelRepository.deleteById(channelId);
    //readStatusRepository.deleteByChannelId(channelId);데베 설정으로 자동 삭제
  }


  private Channel get(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
  }
}
