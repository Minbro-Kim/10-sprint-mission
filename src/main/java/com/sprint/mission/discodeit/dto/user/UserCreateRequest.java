package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.global.annotation.annotation.NotSpaceForFrontBack;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User 생성 정보")
public record UserCreateRequest(
    @NotSpaceForFrontBack
    @Size(max = 50)
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    String username,

    @Size(max = 100)
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일 주소를 입력해주세요.")
    String email,

    @NotBlank(message = "비밀번호를 입력해주세요.")
    String password
) {

}
