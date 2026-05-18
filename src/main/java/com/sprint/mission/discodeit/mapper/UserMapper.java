package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  @Mapping(source = "isOnline", target = "online")
  UserDto toDto(User user, boolean isOnline);


  default User toEntity(UserCreateRequest dto, String encodedPassword, BinaryContent profile) {
    return User.create(
        dto.username(),
        dto.email(),
        encodedPassword,
        profile
    );
  }
}
