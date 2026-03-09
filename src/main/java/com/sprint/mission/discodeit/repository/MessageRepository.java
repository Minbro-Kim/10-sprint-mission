package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("select m from Message m left join fetch m.author left join fetch m.author.profile "
      + "left join fetch m.author.userStatus where m.channel.id = :channelId "
      + "and (cast(:cursor as timestamp) is null or m.createdAt < :cursor)")
  Slice<Message> findAllByChannelIdFetchUserInfo(UUID channelId, Pageable pageable,
      Instant cursor);//널비교때는 타입 추론 이 안되기 때문에 캐스팅 필요

  @Query(value = "SELECT * FROM messages WHERE channel_id = :channelId ORDER BY created_at DESC LIMIT 1",
      nativeQuery = true)
  Optional<Message> findLastMessageByChannelId(@Param("channelId") UUID channelId);

  @Query(value = "SELECT channel_id, Max(created_at) AS maxCreatedAt FROM messages "
      + " WHERE messages.channel_id IN :channelIds "
      + "GROUP BY channel_id",
      nativeQuery = true)
  List<LastMessageTime> findAllLastMessagesByChannelId(Set<UUID> channelIds);

  void deleteByChannelId(UUID channelId);

  public interface LastMessageTime {

    UUID getChannelId();

    Instant getMaxCreatedAt();
  }
}
