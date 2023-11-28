package com.example.dietcommunity.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostException extends RuntimeException{

  private ErrorCode errorCode;
}
