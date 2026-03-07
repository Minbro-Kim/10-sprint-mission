package com.sprint.mission.discodeit.dto.message;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    Object nextCursor,
    int size,
    boolean hasNext,
    Long totalElement
) {

}
