package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContent create(BinaryContentCreateDto dto) {
    BinaryContent content = binaryContentRepository.save(binaryContentMapper.toEntity(dto));
    binaryContentStorage.put(content.getId(), dto.bytes());
    //return binaryContentMapper.toDto(content);
    return content;
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID id) {
    BinaryContent content = binaryContentRepository.findById(id)
        .orElseThrow(() -> new DiscodeitException(ErrorCode.BINARY_CONTENT_NOT_FOUND));
    return binaryContentMapper.toDto(content);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    List<BinaryContentDto> response = binaryContentRepository.findAllById(ids)
        .stream().map(binaryContentMapper::toDto).toList();
    if (response.isEmpty()) {//전부다 유효하지 않은경우만 예외 발생
      throw new DiscodeitException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
    }
    return response;//요청한 아이디중 유효하지 않은 게 있어도 유효한 아이디가 1개 이상이면 정상 반환
  }

  @Override
  public void delete(UUID id) {
    if (!binaryContentRepository.existsById(id)) {
      throw new DiscodeitException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
    }
    binaryContentRepository.deleteById(id);
  }
}
