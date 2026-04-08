package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class UserStatusException extends DiscodeitException {

  protected UserStatusException(ErrorCode errorCode) {
    super(errorCode);
  }
}
