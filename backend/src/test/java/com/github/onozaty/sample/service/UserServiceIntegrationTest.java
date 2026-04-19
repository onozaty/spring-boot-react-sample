package com.github.onozaty.sample.service;

import static org.assertj.core.api.Assertions.*;

import com.github.onozaty.sample.DatabaseResetExtension;
import com.github.onozaty.sample.domain.User;
import com.github.onozaty.sample.domain.UserInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(DatabaseResetExtension.class)
class UserServiceIntegrationTest {

  @Autowired private UserService userService;

  @Test
  void testCreateAndFindById() {
    // Arrange
    UserInput input = new UserInput();
    input.setName("Test User");
    input.setEmail("test@example.com");

    // Act
    User created = userService.create(input);
    User found = userService.findById(created.getId());

    // Assert
    assertThat(created.getId()).isNotNull();
    assertThat(created.getName()).isEqualTo("Test User");
    assertThat(created.getEmail()).isEqualTo("test@example.com");
    assertThat(found.getId()).isEqualTo(created.getId());
    assertThat(found.getName()).isEqualTo("Test User");
  }

  @Test
  void testFindAll() {
    // Arrange
    UserInput input1 = new UserInput();
    input1.setName("User 1");
    input1.setEmail("user1@example.com");
    userService.create(input1);

    UserInput input2 = new UserInput();
    input2.setName("User 2");
    input2.setEmail("user2@example.com");
    userService.create(input2);

    // Act
    var users = userService.findAll();

    // Assert
    assertThat(users).hasSize(2);
  }

  @Test
  void testUpdate() {
    // Arrange
    UserInput input = new UserInput();
    input.setName("Original Name");
    input.setEmail("original@example.com");
    User created = userService.create(input);

    UserInput updatedInput = new UserInput();
    updatedInput.setName("Updated Name");
    updatedInput.setEmail("updated@example.com");

    // Act
    User result = userService.update(created.getId(), updatedInput);

    // Assert
    assertThat(result.getId()).isEqualTo(created.getId());
    assertThat(result.getName()).isEqualTo("Updated Name");
    assertThat(result.getEmail()).isEqualTo("updated@example.com");
  }

  @Test
  void testDelete() {
    // Arrange
    UserInput input = new UserInput();
    input.setName("To Delete");
    input.setEmail("delete@example.com");
    User created = userService.create(input);

    // Act
    userService.delete(created.getId());

    // Assert
    assertThatThrownBy(() -> userService.findById(created.getId()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void testFindByIdNotFound() {
    // Act & Assert
    assertThatThrownBy(() -> userService.findById(999L)).isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void testUpdateNotFound() {
    // Arrange
    UserInput input = new UserInput();
    input.setName("Test");
    input.setEmail("test@example.com");

    // Act & Assert
    assertThatThrownBy(() -> userService.update(999L, input))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void testDeleteNotFound() {
    // Act & Assert
    assertThatThrownBy(() -> userService.delete(999L)).isInstanceOf(UserNotFoundException.class);
  }
}
