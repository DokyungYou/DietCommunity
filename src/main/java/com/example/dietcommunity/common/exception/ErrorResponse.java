package com.example.dietcommunity.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

  private String errorCode;
  private int httpStatus;
  private String errorMessage;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<FieldErrorCustom> errors;



  // 커스텀에러용
  public ErrorResponse(ErrorCode errorCode) {
    this.errorCode = errorCode.name();
    this.httpStatus = errorCode.getHttpStatus();
    this.errorMessage = errorCode.getDescription();
  }


  // MethodArgumentNotValidException 용
  public ErrorResponse(ErrorCode errorCode, BindingResult bindingResult) {
    this.errorCode = errorCode.name();
    this.httpStatus = errorCode.getHttpStatus();
    this.errorMessage = errorCode.getDescription();
    this.errors = FieldErrorCustom.getFieldErrors(bindingResult);
  }


}
