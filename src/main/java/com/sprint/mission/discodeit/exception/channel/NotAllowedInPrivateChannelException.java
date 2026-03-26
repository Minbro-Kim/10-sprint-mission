package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class NotAllowedInPrivateChannelException extends ChannelException {

  public NotAllowedInPrivateChannelException() {
    super(ErrorCode.NOT_ALLOWED_IN_PRIVATE_CHANNEL);
  }
}
