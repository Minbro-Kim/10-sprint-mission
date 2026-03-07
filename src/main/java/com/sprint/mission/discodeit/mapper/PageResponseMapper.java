package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.PageResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

//@Mapper(componentModel = "spring")
//public interface PageResponseMapper {
//
//  @Mapping(target = "totalElement", ignore = true)
//  <T> PageResponse<T> fromSlice(Slice<T> slice);
//
//  //<T> PageResponse<T> fromPage(Page<T> page);
//}
@Mapper(componentModel = "spring")
public interface PageResponseMapper {

  default <T> PageResponse<T> fromSlice(Slice<T> slice, Object nextCursor) {
    return new PageResponse<>(
        slice.getContent(),
        nextCursor,
        slice.getSize(),
        slice.hasNext(),
        null
    );
  }

  default <T> PageResponse<T> fromPage(Page<T> page) {
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.hasNext(),
        page.getTotalElements()
    );
  }
}