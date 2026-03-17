package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path root) {
    this.root = root;
    init();
  }

  private void init() {
    if (Files.notExists(root)) {
      try {
        Files.createDirectories(root);//디렉토리 없으면 생성
      } catch (IOException e) {
        throw new RuntimeException("바이너리 저장소 생성 실패", e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path path = resolvePath(id);
    try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
      fos.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    if (!Files.exists(path)) {
      throw new RuntimeException("파일을 찾을 수 없음" + id);
    }
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    InputStream is = get(binaryContentDto.id());
    Resource resource = new InputStreamResource(is);
    String encodeFile = UriUtils.encode(binaryContentDto.fileName(), StandardCharsets.UTF_8);
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"" + encodeFile + "\"")
        .header("Content-Type", binaryContentDto.contentType())
        .contentLength(binaryContentDto.size())
        .body(resource);
  }
}
