package com.github.onozaty.sample.service;

import static org.assertj.core.api.Assertions.*;

import com.github.onozaty.sample.DatabaseResetExtension;
import com.github.onozaty.sample.domain.User;
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
    User user = new User();
    user.setName("Test User");
    user.setEmail("test@example.com");

    // Act
    User created = userService.create(user);
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
    User user1 = new User();
    user1.setName("User 1");
    user1.setEmail("user1@example.com");
    userService.create(user1);

    User user2 = new User();
    user2.setName("User 2");
    user2.setEmail("user2@example.com");
    userService.create(user2);

    // Act
    var users = userService.findAll();

    // Assert
    assertThat(users).hasSize(2);
  }

  @Test
  void testUpdate() {
    // Arrange
    User user = new User();
    user.setName("Original Name");
    user.setEmail("original@example.com");
    User created = userService.create(user);

    User updatedUser = new User();
    updatedUser.setName("Updated Name");
    updatedUser.setEmail("updated@example.com");

    // Act
    User result = userService.update(created.getId(), updatedUser);

    // Assert
    assertThat(result.getId()).isEqualTo(created.getId());
    assertThat(result.getName()).isEqualTo("Updated Name");
    assertThat(result.getEmail()).isEqualTo("updated@example.com");
  }

  @Test
  void testDelete() {
    // Arrange
    User user = new User();
    user.setName("To Delete");
    user.setEmail("delete@example.com");
    User created = userService.create(user);

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
    User user = new User();
    user.setName("Test");
    user.setEmail("test@example.com");

    // Act & Assert
    assertThatThrownBy(() -> userService.update(999L, user))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void testDeleteNotFound() {
    // Act & Assert
    assertThatThrownBy(() -> userService.delete(999L)).isInstanceOf(UserNotFoundException.class);
  }
}
