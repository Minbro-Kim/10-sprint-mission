package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusDto create(ReadStatusCreateRequest readStatusCreateDto);

  ReadStatus find(UUID readStatusId);

  List<ReadStatusDto> findAllByUserId(UUID userId);

  ReadStatusDto update(UUID id, ReadStatusUpdateRequest readStatusUpdateDto);

  void delete(UUID readStatusId);
}
