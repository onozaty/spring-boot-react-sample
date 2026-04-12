package com.github.onozaty.sample.controller;

import static org.assertj.core.api.Assertions.*;

import com.github.onozaty.sample.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import com.github.onozaty.sample.DatabaseResetExtension;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(DatabaseResetExtension.class)
class UserControllerIntegrationTest {

  @LocalServerPort
  private int port;

  private RestClient restClient;

  @BeforeEach
  void setUp() {
    restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void testCreate() {
    var user = new User();
    user.setName("Test User");
    user.setEmail("test@example.com");

    ResponseEntity<User> response = restClient.post().uri("/api/users")
        .contentType(MediaType.APPLICATION_JSON).body(user).retrieve().toEntity(User.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getHeaders().getLocation()).isNotNull();

    var created = response.getBody();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getName()).isEqualTo("Test User");
    assertThat(created.getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void testFindAll() {
    createUser("User 1", "user1@example.com");
    createUser("User 2", "user2@example.com");

    ResponseEntity<User[]> response =
        restClient.get().uri("/api/users").retrieve().toEntity(User[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  void testFindById() {
    var created = createUser("Test User", "test@example.com");

    ResponseEntity<User> response =
        restClient.get().uri("/api/users/{id}", created.getId()).retrieve().toEntity(User.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getName()).isEqualTo("Test User");
  }

  @Test
  void testFindByIdNotFound() {
    ResponseEntity<Void> response = restClient.get().uri("/api/users/{id}", 999L).retrieve()
        .onStatus(status -> status.is4xxClientError(), (req, res) -> {
        }).toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testUpdate() {
    var created = createUser("Original", "original@example.com");

    var updatedUser = new User();
    updatedUser.setName("Updated");
    updatedUser.setEmail("updated@example.com");

    ResponseEntity<User> response = restClient.put().uri("/api/users/{id}", created.getId())
        .contentType(MediaType.APPLICATION_JSON).body(updatedUser).retrieve().toEntity(User.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getName()).isEqualTo("Updated");
    assertThat(response.getBody().getEmail()).isEqualTo("updated@example.com");
  }

  @Test
  void testUpdateNotFound() {
    var user = new User();
    user.setName("Test");
    user.setEmail("test@example.com");

    ResponseEntity<Void> response =
        restClient.put().uri("/api/users/{id}", 999L).contentType(MediaType.APPLICATION_JSON)
            .body(user).retrieve().onStatus(status -> status.is4xxClientError(), (req, res) -> {
            }).toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testDelete() {
    var created = createUser("To Delete", "delete@example.com");

    ResponseEntity<Void> deleteResponse =
        restClient.delete().uri("/api/users/{id}", created.getId()).retrieve().toBodilessEntity();

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<Void> getResponse = restClient.get().uri("/api/users/{id}", created.getId())
        .retrieve().onStatus(status -> status.is4xxClientError(), (req, res) -> {
        }).toBodilessEntity();

    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testDeleteNotFound() {
    ResponseEntity<Void> response = restClient.delete().uri("/api/users/{id}", 999L).retrieve()
        .onStatus(status -> status.is4xxClientError(), (req, res) -> {
        }).toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  private User createUser(String name, String email) {
    var user = new User();
    user.setName(name);
    user.setEmail(email);
    return restClient.post().uri("/api/users").contentType(MediaType.APPLICATION_JSON).body(user)
        .retrieve().body(User.class);
  }
}
