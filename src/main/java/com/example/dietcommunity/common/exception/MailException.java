package com.example.dietcommunity.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailException extends RuntimeException{

  private ErrorCode errorCode;

}
