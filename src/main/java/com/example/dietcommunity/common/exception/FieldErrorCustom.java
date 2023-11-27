package com.example.dietcommunity.common.exception;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
public class FieldErrorCustom {

  private String field;
  private String value;
  private String message;


  private FieldErrorCustom(FieldError fieldError){
  this.field = fieldError.getField();
  this.value = String.valueOf(fieldError.getRejectedValue());
  this.message = fieldError.getDefaultMessage();
  }


  public static List<FieldErrorCustom> getFieldErrors(BindingResult bindingResult){
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();

    return fieldErrors.stream().map(FieldErrorCustom::new).collect(Collectors.toList());

  }
}
