package com.github.onozaty.sample.controller;

import static org.assertj.core.api.Assertions.*;

import com.github.onozaty.sample.domain.User;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import com.github.onozaty.sample.DatabaseResetExtension;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(DatabaseResetExtension.class)
class UserControllerIntegrationTest {

  private static final ParameterizedTypeReference<Map<String, Object>> PROBLEM_DETAIL_TYPE =
      new ParameterizedTypeReference<>() {};

  @LocalServerPort
  private int port;

  private RestClient restClient;

  @BeforeEach
  void setUp() {
    restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void testCreate() {
    // Arrange
    var user = new User();
    user.setName("Test User");
    user.setEmail("test@example.com");

    // Act
    ResponseEntity<User> response = restClient.post().uri("/api/users")
        .contentType(MediaType.APPLICATION_JSON).body(user).retrieve().toEntity(User.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getHeaders().getLocation()).isNotNull();
    var created = response.getBody();
    assertThat(created.getId()).isNotNull();
    assertThat(created.getName()).isEqualTo("Test User");
    assertThat(created.getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void testFindAll() {
    // Arrange
    createUser("User 1", "user1@example.com");
    createUser("User 2", "user2@example.com");

    // Act
    ResponseEntity<User[]> response =
        restClient.get().uri("/api/users").retrieve().toEntity(User[].class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  void testFindById() {
    // Arrange
    var created = createUser("Test User", "test@example.com");

    // Act
    ResponseEntity<User> response =
        restClient.get().uri("/api/users/{id}", created.getId()).retrieve().toEntity(User.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getName()).isEqualTo("Test User");
  }

  @Test
  void testFindByIdNotFound() {
    // Act
    ResponseEntity<Void> response = restClient.get().uri("/api/users/{id}", 999L).retrieve()
        .onStatus(status -> status.is4xxClientError(), (req, res) -> {
        }).toBodilessEntity();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testUpdate() {
    // Arrange
    var created = createUser("Original", "original@example.com");

    var updatedUser = new User();
    updatedUser.setName("Updated");
    updatedUser.setEmail("updated@example.com");

    // Act
    ResponseEntity<User> response = restClient.put().uri("/api/users/{id}", created.getId())
        .contentType(MediaType.APPLICATION_JSON).body(updatedUser).retrieve().toEntity(User.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getName()).isEqualTo("Updated");
    assertThat(response.getBody().getEmail()).isEqualTo("updated@example.com");
  }

  @Test
  void testUpdateNotFound() {
    // Arrange
    var user = new User();
    user.setName("Test");
    user.setEmail("test@example.com");

    // Act
    ResponseEntity<Void> response =
        restClient.put().uri("/api/users/{id}", 999L).contentType(MediaType.APPLICATION_JSON)
            .body(user).retrieve().onStatus(status -> status.is4xxClientError(), (req, res) -> {
            }).toBodilessEntity();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testDelete() {
    // Arrange
    var created = createUser("To Delete", "delete@example.com");

    // Act
    ResponseEntity<Void> deleteResponse =
        restClient.delete().uri("/api/users/{id}", created.getId()).retrieve().toBodilessEntity();

    // Assert
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    ResponseEntity<Void> getResponse = restClient.get().uri("/api/users/{id}", created.getId())
        .retrieve().onStatus(status -> status.is4xxClientError(), (req, res) -> {
        }).toBodilessEntity();
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testDeleteNotFound() {
    // Act
    ResponseEntity<Void> response = restClient.delete().uri("/api/users/{id}", 999L).retrieve()
        .onStatus(status -> status.is4xxClientError(), (req, res) -> {
        }).toBodilessEntity();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testCreateWithBlankName() {
    // Arrange
    var user = new User();
    user.setName("");
    user.setEmail("test@example.com");

    // Act & Assert
    ResponseEntity<Map<String, Object>> response =
        restClient.post().uri("/api/users").contentType(MediaType.APPLICATION_JSON).body(user)
            .retrieve().onStatus(status -> status.is4xxClientError(), (req, res) -> {
            }).toEntity(PROBLEM_DETAIL_TYPE);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(fieldErrors(response)).anyMatch(e -> "name".equals(e.get("field")));
  }

  @Test
  void testCreateWithInvalidEmail() {
    // Arrange
    var user = new User();
    user.setName("Test User");
    user.setEmail("not-an-email");

    // Act & Assert
    ResponseEntity<Map<String, Object>> response =
        restClient.post().uri("/api/users").contentType(MediaType.APPLICATION_JSON).body(user)
            .retrieve().onStatus(status -> status.is4xxClientError(), (req, res) -> {
            }).toEntity(PROBLEM_DETAIL_TYPE);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(fieldErrors(response)).anyMatch(e -> "email".equals(e.get("field")));
  }

  @Test
  void testUpdateWithBlankName() {
    // Arrange
    var created = createUser("Original", "original@example.com");

    var updatedUser = new User();
    updatedUser.setName("");
    updatedUser.setEmail("updated@example.com");

    // Act & Assert
    ResponseEntity<Map<String, Object>> response = restClient.put()
        .uri("/api/users/{id}", created.getId()).contentType(MediaType.APPLICATION_JSON)
        .body(updatedUser).retrieve().onStatus(status -> status.is4xxClientError(), (req, res) -> {
        }).toEntity(PROBLEM_DETAIL_TYPE);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(fieldErrors(response)).anyMatch(e -> "name".equals(e.get("field")));
  }

  @Test
  void testUpdateWithInvalidEmail() {
    // Arrange
    var created = createUser("Original", "original@example.com");

    var updatedUser = new User();
    updatedUser.setName("Updated");
    updatedUser.setEmail("not-an-email");

    // Act & Assert
    ResponseEntity<Map<String, Object>> response = restClient.put()
        .uri("/api/users/{id}", created.getId()).contentType(MediaType.APPLICATION_JSON)
        .body(updatedUser).retrieve().onStatus(status -> status.is4xxClientError(), (req, res) -> {
        }).toEntity(PROBLEM_DETAIL_TYPE);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(fieldErrors(response)).anyMatch(e -> "email".equals(e.get("field")));
  }

  private User createUser(String name, String email) {
    var user = new User();
    user.setName(name);
    user.setEmail(email);
    return restClient.post().uri("/api/users").contentType(MediaType.APPLICATION_JSON).body(user)
        .retrieve().body(User.class);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, String>> fieldErrors(ResponseEntity<Map<String, Object>> response) {
    return (List<Map<String, String>>) response.getBody().get("errors");
  }
}
