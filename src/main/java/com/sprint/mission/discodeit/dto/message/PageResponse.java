package com.sprint.mission.discodeit.dto.message;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int number,
    int size,
    boolean hasNext,
    Long totalElement
) {

}
