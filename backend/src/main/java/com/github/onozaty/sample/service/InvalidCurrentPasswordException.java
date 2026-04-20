package com.github.onozaty.sample.service;

public class InvalidCurrentPasswordException extends RuntimeException {

  public InvalidCurrentPasswordException() {
    super("Current password is incorrect");
  }
}
