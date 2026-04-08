package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

  @Modifying
  @Query("DELETE FROM BinaryContent b WHERE b.id IN "
      + "(SELECT a.id FROM Message m JOIN m.attachments a WHERE m.channel.id = :channelId)")
  void bulkDeleteByChannelId(UUID channelId);
}
