package com.example.dietcommunity.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

    log.error("errorCode: {}", ErrorCode.INVALID_INPUT_VALUE);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult()));

  }

  @ExceptionHandler(MemberException.class)
  public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {

    log.error("errorCode: {}", e.getErrorCode());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(e.getErrorCode()));

  }

  @ExceptionHandler(MailException.class)
  public ResponseEntity<ErrorResponse> handleMailException(MailException e) {

    log.error("{} is occurred", e.getErrorCode());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(e.getErrorCode()));
  }

  @ExceptionHandler(InvalidAuthCodeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidAuthCodeException(InvalidAuthCodeException e) {
    log.error("{} is occurred", e.getErrorCode());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getErrorCode()));
  }

}
