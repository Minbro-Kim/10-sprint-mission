package com.sprint.mission.discodeit.util.listener;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    //채널이름, 보낸사람, 메세지 내용
    //활성화 리드스테이터스 조회
    //해당 사람들에 대해 알림 생성(본인 제외)
    String title = event.authorName() + " (#" + event.channelName() + ")";
    readStatusRepository.findAllByChannelIdAndNotificationEnabledAndUserIdNot(event.channelId(),
            true, event.authorId())
        .forEach(r -> {
          notificationService.create(
              new NotificationCreateRequest(r.getUser().getId(), title, event.content()));
        });
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    //권한 변경 당사자에게 알림
    String title = "권한이 변경되었습니다.";
    String content = event.beforeRole() + " -> " + event.afterRole();
    notificationService.create(new NotificationCreateRequest(event.userId(), title, content));
  }

}
