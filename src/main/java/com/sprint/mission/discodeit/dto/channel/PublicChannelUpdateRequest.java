package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.global.annotation.NotSpace;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(
    @JsonProperty("newName")
    @NotSpace
    String name,

    @NotSpace
    @JsonProperty("newDescription")
    String description
) {

}
