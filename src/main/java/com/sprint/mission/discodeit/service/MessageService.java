package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;

import com.sprint.mission.discodeit.dto.message.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

  MessageDto create(MessageCreateRequest messageCreateDto,
      List<BinaryContentCreateDto> binaryContentCreateDtos);

  MessageDto find(UUID messageId);

  PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);

  MessageDto update(UUID id, MessageUpdateRequest messageUpdateDto);

  void delete(UUID messageId);
}
