package com.github.onozaty.sample.controller;

import com.github.onozaty.sample.service.UserNotFoundException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Void> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.notFound().build();
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    List<ValidationProblemDetail.FieldError> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fe -> new ValidationProblemDetail.FieldError(fe.getField(), fe.getDefaultMessage()))
            .toList();
    var problemDetail = ex.getBody();
    var body =
        new ValidationProblemDetail(
            problemDetail.getTitle(),
            status.value(),
            problemDetail.getDetail(),
            ((ServletWebRequest) request).getRequest().getRequestURI(),
            errors);
    return ResponseEntity.status(status).headers(headers).body(body);
  }
}
