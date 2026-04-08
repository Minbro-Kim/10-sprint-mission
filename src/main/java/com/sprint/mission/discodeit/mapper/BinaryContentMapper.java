package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.InputStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  BinaryContentDto toDto(BinaryContent binaryContent);

  default BinaryContent toEntity(BinaryContentCreateDto dto) {
    return BinaryContent.create(
        dto.fileName(),
        dto.contentType(),
        dto.size()
    );
  }

  default BinaryContentCreateDto toCreateDto(MultipartFile multipartFile) {
    try {
      return new BinaryContentCreateDto(multipartFile.getOriginalFilename(),
          multipartFile.getContentType(),
          multipartFile.getBytes(),
          multipartFile.getSize());
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}