package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.InvalidMessageException;
import com.sprint.mission.discodeit.exception.message.MessageAuthorOnlyException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  //
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final MessageMapper messageMapper;
  private final ReadStatusRepository readStatusRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final PageResponseMapper pageResponseMapper;

  @Override
  public MessageDto create(MessageCreateRequest dto,
      List<BinaryContentCreateDto> binaryContentCreateDtos) {
    log.debug("메세지 생성 시도: channelId={}, authorId={}", dto.channelId(), dto.authorId());
    User user = userRepository.findById(dto.authorId())
        .orElseThrow(() -> new UserNotFoundException().addDetail("userId", dto.authorId()));
    Channel channel = channelRepository.findById(dto.channelId())
        .orElseThrow(() -> new ChannelNotFoundException().addDetail("channelId", dto.channelId()));
    checkMember(dto.channelId(), dto.authorId());
    checkValidate(dto, binaryContentCreateDtos);
    List<BinaryContent> attachments = new ArrayList<>();
    binaryContentCreateDtos
        .forEach((attachment) -> {
          BinaryContent content = binaryContentMapper.toEntity(attachment);
          attachments.add(content);
        });
    Message message = messageMapper.toEntity(dto, user, channel, attachments);
    messageRepository.save(message);
    for (int i = 0; i < binaryContentCreateDtos.size(); i++) {
      binaryContentStorage.put(attachments.get(i).getId(), binaryContentCreateDtos.get(i).bytes());
      log.debug("메세지 첨부파일 저장: messageId={}, binaryContentId={}", message.getId(),
          attachments.get(i).getId());
    }
    log.info("메세지 생성 성공: channelId={}, authorId={}, messageId={}", channel.getId(), user.getId(),
        message.getId());
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    Message message = get(messageId);
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable,
      Instant cursor) {
    //checkMember(channelId, userId);//인증 인가 도입하고 실행
//    return messageRepository.findAllByChannelId(channelId,pageable).stream()
//        .map(messageMapper::toDto).toList();
    Slice<Message> slice = messageRepository.findAllByChannelIdFetchUserInfo(channelId, pageable,
        cursor);
    List<UUID> messageIds = slice.stream().map(Message::getId).toList();
    Map<UUID, List<BinaryContent>> attachmentMap = new HashMap<>();//바이너리 컨텐츠 한번에 가져오기
    messageRepository.findAllByIdInFetchAttachments(messageIds)
        .forEach((m) -> {
          //repo에서 패치로 바이너리 컨텐츠 가져오면 내부 순서가 반대로 되기때문에 순서 뒤집어야함
          List<BinaryContent> reversedList = new ArrayList<>(m.getAttachments());
          Collections.reverse(reversedList);
          attachmentMap.put(m.getId(), reversedList);
        });
    Slice<MessageDto> sliceDto = slice.map(
        s -> messageMapper.toDto(s, attachmentMap.get(s.getId())));
    Instant nextCursor = null;
    if (slice.hasNext() && slice.hasContent()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).getCreatedAt();
    }
    PageResponse<MessageDto> response = pageResponseMapper.fromSlice(sliceDto, nextCursor);
    return response;
  }

  @Override
  public MessageDto update(UUID id, MessageUpdateRequest dto) {
    log.debug("메세지 수정 시도: messageId={}", id);
    Message message = get(id);
    //인증인가 구현후 아래 유효성 검증 도입
    //checkMember(message.getChannelId(), userId);
    //checkAuthor(message.getAuthorId(), userId);
    message.update(dto.newContent(), null);//첨부파일 변경을 하려면 별도로 메서드 필요
    log.info("메세지 수정 성공:  messageId={}", message.getId());
    return messageMapper.toDto(message);
  }

  @Override
  public void delete(UUID messageId) {
    log.debug("메세지 삭제 시도: messageId={}", messageId);
    Message message = get(messageId);
    // 아래 유효성 검증은 인증/인가 추가후
    //checkMember(message.getChannelId(), userId);
    //checkAuthor(message.getAuthorId(), userId);
//    if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
//      message.getAttachments()
//          .forEach(b -> binaryContentRepository.deleteById(b.getId()));//첨부파일 있는경우만 지우기
//    }
    log.info("메세지 삭제 성공: messageId={}", message.getId());
    messageRepository.deleteById(messageId);

  }

  private void checkValidate(MessageCreateRequest dto,
      List<BinaryContentCreateDto> binaryContentCreateDtos) {
    if ((dto.content() == null || dto.content().isEmpty()) //컨텐츠와 첨부파일 두개다 없는 경우
        && (binaryContentCreateDtos == null || binaryContentCreateDtos.isEmpty())) {
      log.warn("메세지에 컨텐츠와 첨부파일 둘다 없음");
      throw new InvalidMessageException();
    }
  }

  private void checkMember(UUID channelId, UUID userId) {
    if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isEmpty()) {
      log.warn("해당 채널의 멤버가 아님: channelId={}, userId={}", channelId, userId);
      throw new ReadStatusNotFoundException().addDetail("channelId", channelId)
          .addDetail("userId", userId);
    }
  }

  private Message get(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException().addDetail("messageId", messageId));
  }

  private void checkAuthor(UUID authorId, UUID userId) {
    if (!authorId.equals(userId)) {
      throw new MessageAuthorOnlyException().addDetail("authorId", authorId)
          .addDetail("userId", userId);
    }
  }
}
