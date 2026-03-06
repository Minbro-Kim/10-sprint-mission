package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  @Mapping(source = "userStatus.online", target = "online")
  UserDto toDto(User user);

  /*
  public UserDto toDto(User user, UserStatus userStatus) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile().getId(),
        userStatus.isOnline(),
        user.getCreatedAt(),
        user.getUpdatedAt()
    );
  }
  */

  default User toEntity(UserCreateRequest dto, BinaryContent profile) {
    return User.create(
        dto.username(),
        dto.email(),
        dto.password(),
        profile
    );
  }
}
