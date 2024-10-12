package com.ovg.flipper.handler;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    BindingResult bindingResult = ex.getBindingResult();

    Map<String, String> errorMap = new HashMap<>();
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(UsernameNotFoundException.class)
  protected ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}