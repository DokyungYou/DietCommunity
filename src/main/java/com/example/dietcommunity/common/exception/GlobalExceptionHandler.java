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


  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {

    log.error("errorCode: {} , errorMessage: {}", ErrorCode.NULL_POINTER_EXCEPTION, e.getMessage());
    return ResponseEntity.status(500)
        .body(new ErrorResponse(ErrorCode.NULL_POINTER_EXCEPTION));

  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {

    log.error("errorCode: {} , errorMessage: {}", ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION, e.getMessage());
    return ResponseEntity.status(400)
        .body(new ErrorResponse( ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION));

  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

    log.error("errorCode: {}", ErrorCode.INVALID_INPUT_VALUE);
    return ResponseEntity.status(400)
        .body(new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult()));

  }



  // filter 내에서 발생한 SecurityExceptionCustom 처리하는데 사용되는 핸들러 X
  @ExceptionHandler(SecurityExceptionCustom.class)
  public ResponseEntity<ErrorResponse> handleSecurityExceptionCustom(SecurityExceptionCustom e) {

    log.error("errorCode: {}", e.getErrorCode());
    return ResponseEntity.status(e.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(e.getErrorCode()));

  }


  @ExceptionHandler(MemberException.class)
  public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {

    log.error("errorCode: {}", e.getErrorCode());
    return ResponseEntity.status(e.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(e.getErrorCode()));

  }

  @ExceptionHandler(MailException.class)
  public ResponseEntity<ErrorResponse> handleMailException(MailException e) {

    log.error("{} is occurred", e.getErrorCode());
    return ResponseEntity.status(e.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(e.getErrorCode()));
  }

  @ExceptionHandler(InvalidAuthCodeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidAuthCodeException(InvalidAuthCodeException e) {

    log.error("{} is occurred", e.getErrorCode());
    return ResponseEntity.status(e.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(e.getErrorCode()));
  }

}
