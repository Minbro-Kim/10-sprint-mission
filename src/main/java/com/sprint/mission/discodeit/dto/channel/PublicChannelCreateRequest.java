package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.global.annotation.annotation.NotSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(
    @NotBlank
    String name,

    @NotSpace
    String description
) {

  public PublicChannelCreateRequest {
    name = name.trim();
    description = description == null ? null : description.trim();
  }
}
