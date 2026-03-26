package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 기본 에러
  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorResponse response = ErrorResponse.of(e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  @ExceptionHandler(value = DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(DiscodeitException e) {
    ErrorResponse response = ErrorResponse.of(e);
    return ResponseEntity.status(HttpStatus.valueOf(e.getErrorCode().getStatus())).body(response);
  }

  //파라미터
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    return ResponseEntity.badRequest().body(ErrorResponse.of(e));
  }

  //경로
  @ExceptionHandler(value = ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    return ResponseEntity.badRequest().body(ErrorResponse.of(e));
  }
}