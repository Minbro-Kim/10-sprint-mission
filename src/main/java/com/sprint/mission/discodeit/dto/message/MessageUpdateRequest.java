package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "수정할 Message 내용")
public record MessageUpdateRequest(
    @NotNull //메세지는 공백 메세지 가능
    String newContent
) {

}
