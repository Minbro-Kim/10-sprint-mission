package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class EmailAlreadyExistException extends UserException {

  public EmailAlreadyExistException(){
    super(ErrorCode.EMAIL_ALREADY_EXIST);
  }
}
