package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelAlreadyExistException extends ChannelException {

  public ChannelAlreadyExistException() {
    super(ErrorCode.CHANNEL_ALREADY_EXIST);
  }
}
