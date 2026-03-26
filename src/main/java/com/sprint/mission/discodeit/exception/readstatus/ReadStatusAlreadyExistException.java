package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusAlreadyExistException extends ReadStatusException {

  public ReadStatusAlreadyExistException() {
    super(ErrorCode.READ_STATUS_ALREADY_EXIST);
  }
}
