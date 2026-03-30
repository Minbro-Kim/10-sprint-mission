package com.sprint.mission.discodeit.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.message.InvalidMessageException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MessageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserStatusRepository userStatusRepository;
  @Autowired
  private BinaryContentRepository binaryContentRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;
  @Autowired
  private EntityManager em;


  private UUID u1Id;
  private UUID u2Id;
  private UUID u3Id;
  private UUID c1Id;
  private UUID c2Id;
  private UUID m1Id;
  private UUID m2Id;

  @BeforeEach
  public void setup() {
    binaryContentRepository.deleteAll();
    messageRepository.deleteAll();
    readStatusRepository.deleteAll();
    userStatusRepository.deleteAll();
    userRepository.deleteAll();
    channelRepository.deleteAll();

    BinaryContent profile1 = BinaryContent.create("profile1", "jpg", 50L);
    User user1 = User.create("test1", "test1@test.com", "test123", profile1);
    UserStatus.create(user1, Instant.parse("2020-01-01T00:00:00.00Z"));
    BinaryContent profile2 = BinaryContent.create("profile2", "jpg", 50L);
    User user2 = User.create("test2", "test2@test.com", "test123", profile2);
    UserStatus.create(user2, Instant.parse("2020-01-01T00:00:00.00Z"));
    BinaryContent profile3 = BinaryContent.create("profile3", "jpg", 50L);
    User user3 = User.create("tes3", "test3@test.com", "test123", profile3);
    UserStatus.create(user3, Instant.parse("2020-01-01T00:00:00.00Z"));
    userRepository.saveAll(List.of(user1, user2, user3));

    u1Id = user1.getId();
    u2Id = user2.getId();
    u3Id = user3.getId();

    em.flush();
    em.clear();

    Channel publicChannel = Channel.create(ChannelType.PUBLIC, "public", "description");
    channelRepository.save(publicChannel);
    readStatusRepository.save(
        ReadStatus.create(user1, publicChannel, Instant.parse("2020-01-01T00:00:00.00Z")));
    readStatusRepository.save(
        ReadStatus.create(user2, publicChannel, Instant.parse("2020-01-01T00:00:00.00Z")));
    readStatusRepository.save(
        ReadStatus.create(user3, publicChannel, Instant.parse("2020-01-01T00:00:00.00Z")));

    Channel privateChannel = Channel.create(ChannelType.PRIVATE, "private", "description");
    channelRepository.save(privateChannel);
    readStatusRepository.save(
        ReadStatus.create(user2, privateChannel, Instant.parse("2020-01-04T00:00:00.00Z")));
    readStatusRepository.save(
        ReadStatus.create(user3, privateChannel, Instant.parse("2020-01-04T00:00:00.00Z")));

    c1Id = publicChannel.getId();
    c2Id = privateChannel.getId();
    em.flush();
    em.clear();

    BinaryContent bc1 = BinaryContent.create("bc1", "jpg", 50L);
    BinaryContent bc2 = BinaryContent.create("bc2", "jpg", 50L);
    BinaryContent bc3 = BinaryContent.create("bc3", "jpg", 50L);
    BinaryContent bc4 = BinaryContent.create("bc4", "jpg", 50L);
    BinaryContent bc5 = BinaryContent.create("bc5", "jpg", 50L);
    List<BinaryContent> attachments1 = List.of(bc1, bc2);
    List<BinaryContent> attachments2 = List.of(bc3);
    Message message1 = Message.create("m1", publicChannel, user1, attachments1);
    Message message2 = Message.create("m2", publicChannel, user2, attachments2);
    messageRepository.saveAll(List.of(message1, message2));
    m1Id = message1.getId();
    m2Id = message2.getId();

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("성공: 첨부파일과 함께 메세지 전송 성공(201 created)")
  void sendMessageWithAttachmentsSuccess() throws Exception {
    //given
    MessageCreateRequest request = new MessageCreateRequest("testContent", c1Id, u1Id);

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // 첨부파일 생성
    // 첨부파일 1
    MockMultipartFile attachmentsPart1 = new MockMultipartFile(
        "attachments",
        "myImage",
        MediaType.IMAGE_JPEG_VALUE,
        "test-image-content".getBytes()
    );
    // 첨부파일 2
    MockMultipartFile attachmentsPart2 = new MockMultipartFile(
        "attachments",
        "myTxt.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "text-content".getBytes()
    );

    //when
    MvcResult result = mockMvc.perform(multipart("/api/messages")
            .file(userPart)
            .file(attachmentsPart1)
            .file(attachmentsPart2)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    //then
    String content = result.getResponse().getContentAsString();
    String createdId = JsonPath.read(content, "$.id");
    UUID newMessageId = UUID.fromString(createdId);

    em.flush();
    em.clear();

    Message message = messageRepository.findById(newMessageId).orElseThrow();
    assertEquals(2, message.getAttachments().size());
    assertEquals(request.content(), message.getContent());
  }

  @Test
  @DisplayName("실패: 빈 메세지 전송 실패(400 bad request)")
  void sendMessageWithNoContentFailure() throws Exception {
    //given
    MessageCreateRequest request = new MessageCreateRequest("", c1Id, u1Id);

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    //when & then
    mockMvc.perform(multipart("/api/messages")
            .file(userPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.exceptionType").value(InvalidMessageException.class.getSimpleName()));
  }


  @Test
  @DisplayName("성공: 메세지 내용 변경 성공(200 ok)")
  void updateMessageContentSuccess() throws Exception {
    //given
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");

    //when & then
    mockMvc.perform(patch("/api/messages/{messageId}", m1Id)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(m1Id.toString()))
        .andExpect(jsonPath("$.content").value(request.newContent()));

    em.flush();
    em.clear();

    Message message = messageRepository.findById(m1Id).orElseThrow();
    assertEquals(request.newContent(), message.getContent());
  }

  @Test
  @DisplayName("실패: 유효하지 않은 메세지 아이디에 대해 메세지 내용 변경 실패(404 not found)")
  void updateMessageContentWithNotExistedMessageIdFailure() throws Exception {
    //given
    UUID wrongMessageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");

    //when & then
    mockMvc.perform(patch("/api/messages/{messageId}", wrongMessageId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.exceptionType").value(MessageNotFoundException.class.getSimpleName()));
  }


  @Test
  @DisplayName("성공: 메세지 삭제 성공(204 No content)")
  void deleteMessageSuccess() throws Exception {
    //when & then
    mockMvc.perform(delete("/api/messages/{messageId}", m1Id)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    em.flush();
    em.clear();

    assertTrue(messageRepository.findById(m1Id).isEmpty());
  }

  @Test
  @DisplayName("실패: 유효하지 않은 메세지 아이디로 사용자 삭제 실패(404 Not found)")
  void deleteUserByWrongUserIdFailure() throws Exception {
    //given
    UUID wrongMessageId = UUID.randomUUID();

    //when & then
    mockMvc.perform(delete("/api/messages/{messageId}", wrongMessageId)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.exceptionType").value(MessageNotFoundException.class.getSimpleName()));

  }
}
