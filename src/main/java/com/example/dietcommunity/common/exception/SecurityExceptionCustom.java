package com.example.dietcommunity.common.exception;

import lombok.Getter;

@Getter
public class SecurityExceptionCustom extends RuntimeException{
  ErrorCode errorCode;

  public SecurityExceptionCustom(ErrorCode errorCode){
    super(errorCode.getDescription());
    this.errorCode = errorCode;
  }

}
