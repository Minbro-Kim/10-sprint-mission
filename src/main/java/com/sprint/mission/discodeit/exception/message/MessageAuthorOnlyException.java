package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageAuthorOnlyException extends MessageException {

  public MessageAuthorOnlyException() {
    super(ErrorCode.MESSAGE_AUTHOR_ONLY);
  }
}
