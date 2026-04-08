package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {
    /*
    ### **바이너리 파일 다운로드**
    - [ ]  바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
     */

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Operation(summary = "첨부 파일 조회", operationId = "find")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
      @ApiResponse(
          responseCode = "404",
          description = "첨부 파일을 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class),
              examples = @ExampleObject(value = """
                      {
                        "fieldErrors": null,
                        "violationErrors": null,
                        "code": 404,
                        "message": "존재하지 않는 바이너리 컨텐츠"
                      }
                  """)
          )
      )
  })
  @Parameters(value = {@Parameter(name = "binaryContentId", description = "조회할 첨부 파일 ID")})
  @GetMapping(path = "/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> findProfileImage(@PathVariable UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.find(binaryContentId));
  }

  @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn",
      parameters = @Parameter(name = "binaryContentIds", description = "조회할 첨부 파일 ID 목록"))
  @ApiResponse(responseCode = "200", description = "첨부파일 목록 조회 성공")
  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findImages(
      @RequestParam List<UUID> binaryContentIds) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }

  @Operation(summary = "파일 다운로드", operationId = "download")
  @Parameters(value = {@Parameter(name = "binaryContentId", description = "다운로드할 파일 ID")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "파일 다운로드 성공", content = @Content(schema = @Schema(implementation = Resource.class))),
  })
  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> downloadBinaryContent(@PathVariable UUID binaryContentId) {
    BinaryContentDto dto = binaryContentService.find(binaryContentId);
    return binaryContentStorage.download(dto);
  }
}
