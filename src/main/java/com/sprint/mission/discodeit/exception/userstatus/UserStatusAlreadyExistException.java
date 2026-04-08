package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistException extends UserStatusException {

  public UserStatusAlreadyExistException() {
    super(ErrorCode.USER_STATUS_ALREADY_EXIST);
  }
}
