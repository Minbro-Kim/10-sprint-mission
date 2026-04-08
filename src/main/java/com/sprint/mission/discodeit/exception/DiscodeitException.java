package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  protected DiscodeitException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = new LinkedHashMap<>();
  }

  public DiscodeitException addDetail(String key, Object value) {
    this.details.put(key, value);
    return this;//체이닝 가능
  }
}
