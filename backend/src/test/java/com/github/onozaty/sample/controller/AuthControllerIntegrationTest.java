package com.github.onozaty.sample.controller;

import static org.assertj.core.api.Assertions.*;

import com.github.onozaty.sample.AppTest;
import com.github.onozaty.sample.domain.User;
import com.github.onozaty.sample.service.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@AppTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

  @LocalServerPort private int port;

  private RestClient restClient;

  @BeforeEach
  void setUp() {
    restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void testLoginSuccess() {
    // Act
    ResponseEntity<User> response =
        restClient
            .post()
            .uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"email\":\"admin@example.com\",\"password\":\"admin\"}")
            .retrieve()
            .toEntity(User.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getEmail()).isEqualTo("admin@example.com");

    var setCookieHeaders = response.getHeaders().get("Set-Cookie");
    assertThat(setCookieHeaders).isNotNull();
    String authCookie =
        setCookieHeaders.stream()
            .filter(v -> v.startsWith(JwtTokenService.COOKIE_NAME + "="))
            .findFirst()
            .orElse(null);
    assertThat(authCookie).isNotNull();
    assertThat(authCookie).contains("HttpOnly");
    assertThat(authCookie).contains("SameSite=Strict");
    assertThat(authCookie).contains("Path=/");
    assertThat(authCookie).contains("Max-Age=");
  }

  @Test
  void testLoginWithWrongPassword() {
    // Act & Assert
    ResponseEntity<Void> response =
        restClient
            .post()
            .uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"email\":\"admin@example.com\",\"password\":\"wrongpassword\"}")
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
            .toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testLoginWithUnknownEmail() {
    // Act & Assert
    ResponseEntity<Void> response =
        restClient
            .post()
            .uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"email\":\"unknown@example.com\",\"password\":\"admin\"}")
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
            .toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testLogout() {
    // Arrange
    String cookie = login();

    // Act
    ResponseEntity<Void> response =
        restClient
            .post()
            .uri("/api/auth/logout")
            .header("Cookie", cookie)
            .retrieve()
            .toBodilessEntity();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    var setCookieHeaders = response.getHeaders().get("Set-Cookie");
    assertThat(setCookieHeaders).isNotNull();
    String authCookie =
        setCookieHeaders.stream()
            .filter(v -> v.startsWith(JwtTokenService.COOKIE_NAME + "="))
            .findFirst()
            .orElse(null);
    assertThat(authCookie).isNotNull();
    assertThat(authCookie).contains("Max-Age=0");
  }

  @Test
  void testGetMe() {
    // Arrange
    String cookie = login();

    // Act
    ResponseEntity<User> response =
        restClient
            .get()
            .uri("/api/auth/me")
            .header("Cookie", cookie)
            .retrieve()
            .toEntity(User.class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getEmail()).isEqualTo("admin@example.com");
  }

  @Test
  void testGetMeUnauthenticated() {
    // Act & Assert
    ResponseEntity<Void> response =
        restClient
            .get()
            .uri("/api/auth/me")
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
            .toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testChangePassword() {
    // Arrange
    String cookie = login();

    // Act
    ResponseEntity<Void> response =
        restClient
            .patch()
            .uri("/api/auth/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Cookie", cookie)
            .body("{\"currentPassword\":\"admin\",\"newPassword\":\"newpassword123\"}")
            .retrieve()
            .toBodilessEntity();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // 新しいパスワードでログインできる
    ResponseEntity<User> loginResponse =
        restClient
            .post()
            .uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"email\":\"admin@example.com\",\"password\":\"newpassword123\"}")
            .retrieve()
            .toEntity(User.class);
    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void testChangePasswordWithWrongCurrentPassword() {
    // Arrange
    String cookie = login();

    // Act & Assert
    ResponseEntity<Void> response =
        restClient
            .patch()
            .uri("/api/auth/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Cookie", cookie)
            .body("{\"currentPassword\":\"wrongpassword\",\"newPassword\":\"newpassword123\"}")
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
            .toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testUnauthenticatedAccessToUsers() {
    // Act & Assert
    ResponseEntity<Void> response =
        restClient
            .get()
            .uri("/api/users")
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
            .toBodilessEntity();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private String login() {
    var response =
        restClient
            .post()
            .uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"email\":\"admin@example.com\",\"password\":\"admin\"}")
            .retrieve()
            .toBodilessEntity();

    return response.getHeaders().get("Set-Cookie").stream()
        .filter(v -> v.startsWith(JwtTokenService.COOKIE_NAME + "="))
        .map(v -> v.split(";")[0])
        .findFirst()
        .orElseThrow();
  }
}
