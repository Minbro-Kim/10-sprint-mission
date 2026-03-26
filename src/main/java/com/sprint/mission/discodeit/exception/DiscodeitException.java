package com.sprint.mission.discodeit.exception;

import lombok.Getter;

public class DiscodeitException extends RuntimeException {

  @Getter
  private final ErrorCode exceptionCode;

  public DiscodeitException(ErrorCode exceptionCode) {
    super(exceptionCode.getMessage());
    this.exceptionCode = exceptionCode;
  }

}
