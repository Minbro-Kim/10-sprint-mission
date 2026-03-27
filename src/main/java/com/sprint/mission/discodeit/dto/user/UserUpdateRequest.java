package com.sprint.mission.discodeit.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.global.annotation.NotSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
    @JsonProperty("newUsername")
    @NotSpace
    String username,

    @NotSpace
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @JsonProperty("newEmail")
    String email,

    @NotSpace
    @JsonProperty("newPassword")
    String password
) {

}
