package com.github.onozaty.sample.service;

import static org.assertj.core.api.Assertions.*;

import com.github.onozaty.sample.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.onozaty.sample.DatabaseResetExtension;

@SpringBootTest
@ExtendWith(DatabaseResetExtension.class)
class UserServiceIntegrationTest {

  @Autowired
  private UserService userService;

  @Test
  void testCreateAndFindById() {
    // ユーザー作成
    User user = new User();
    user.setName("Test User");
    user.setEmail("test@example.com");
    User created = userService.create(user);

    assertThat(created.getId()).isNotNull();
    assertThat(created.getName()).isEqualTo("Test User");
    assertThat(created.getEmail()).isEqualTo("test@example.com");

    // IDから検索
    User found = userService.findById(created.getId());
    assertThat(found.getId()).isEqualTo(created.getId());
    assertThat(found.getName()).isEqualTo("Test User");
  }

  @Test
  void testFindAll() {
    // ユーザー作成
    User user1 = new User();
    user1.setName("User 1");
    user1.setEmail("user1@example.com");
    userService.create(user1);

    User user2 = new User();
    user2.setName("User 2");
    user2.setEmail("user2@example.com");
    userService.create(user2);

    // 全件取得
    var users = userService.findAll();
    assertThat(users).hasSize(2);
  }

  @Test
  void testUpdate() {
    // ユーザー作成
    User user = new User();
    user.setName("Original Name");
    user.setEmail("original@example.com");
    User created = userService.create(user);

    // 更新
    User updatedUser = new User();
    updatedUser.setName("Updated Name");
    updatedUser.setEmail("updated@example.com");
    User result = userService.update(created.getId(), updatedUser);

    assertThat(result.getId()).isEqualTo(created.getId());
    assertThat(result.getName()).isEqualTo("Updated Name");
    assertThat(result.getEmail()).isEqualTo("updated@example.com");
  }

  @Test
  void testDelete() {
    // ユーザー作成
    User user = new User();
    user.setName("To Delete");
    user.setEmail("delete@example.com");
    User created = userService.create(user);

    // 削除
    userService.delete(created.getId());

    // 存在確認
    assertThatThrownBy(() -> userService.findById(created.getId()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void testFindByIdNotFound() {
    // 存在しないIDで検索
    assertThatThrownBy(() -> userService.findById(999L)).isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void testUpdateNotFound() {
    // 存在しないIDで更新
    User user = new User();
    user.setName("Test");
    user.setEmail("test@example.com");

    assertThatThrownBy(() -> userService.update(999L, user))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void testDeleteNotFound() {
    // 存在しないIDで削除
    assertThatThrownBy(() -> userService.delete(999L)).isInstanceOf(UserNotFoundException.class);
  }
}
