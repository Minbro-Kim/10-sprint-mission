package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  @Query(value = "SELECT * FROM messages WHERE channel_id = :channelId ORDER BY created_at DESC LIMIT 1",
      nativeQuery = true)
  Optional<Message> findLastMessageByChannelId(@Param("channelId") UUID channelId);

  void deleteByChannelId(UUID channelId);
}
