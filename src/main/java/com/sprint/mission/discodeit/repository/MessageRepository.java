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

  @Query("select m from Message m left join fetch m.author left join fetch m.author.profile left join fetch m.author.userStatus where m.channel.id = :channelId")
  Slice<Message> findAllByChannelIdFetchUserInfo(UUID channelId, Pageable pageable);

  @Query(value = "SELECT * FROM messages WHERE channel_id = :channelId ORDER BY created_at DESC LIMIT 1",
      nativeQuery = true)
  Optional<Message> findLastMessageByChannelId(@Param("channelId") UUID channelId);

  void deleteByChannelId(UUID channelId);
}
