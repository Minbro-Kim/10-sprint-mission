package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.global.annotation.annotation.NotSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(
    @Size(max = 100)
    @JsonProperty("newName")
    @NotSpace
    String name,

    @Size(max = 500)
    @NotSpace
    @JsonProperty("newDescription")
    String description
) {

  public PublicChannelUpdateRequest {
    name = name == null ? null : name.trim();
    description = description == null ? null : description.trim();
  }
}
