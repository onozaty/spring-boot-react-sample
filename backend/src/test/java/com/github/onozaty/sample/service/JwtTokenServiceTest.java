package com.github.onozaty.sample.service;

import static org.assertj.core.api.Assertions.*;

import com.github.onozaty.sample.config.CookieProperties;
import com.github.onozaty.sample.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtException;

class JwtTokenServiceTest {

  private static final String SECRET = "test-secret-key-minimum-32-bytes-for-hmac-sha256!!";

  private JwtTokenService service(int expirationMinutes, int refreshThresholdMinutes) {
    return new JwtTokenService(
        new JwtProperties(SECRET, expirationMinutes, refreshThresholdMinutes),
        new CookieProperties(false));
  }

  @Test
  void testIssueAndDecode() {
    // Arrange
    var svc = service(480, 240);

    // Act
    String token = svc.issue(1L, "user@example.com");
    var jwt = svc.decode(token);

    // Assert
    assertThat(jwt.getSubject()).isEqualTo("user@example.com");
    assertThat(jwt.<Long>getClaim(JwtTokenService.CLAIM_USER_ID)).isEqualTo(1L);
    assertThat(jwt.getExpiresAt()).isNotNull();
    assertThat(jwt.getIssuedAt()).isNotNull();
  }

  @Test
  void testNeedsRefreshWhenRemainingBelowThreshold() {
    // Arrange — 有効期間 2 分、閾値 3 分 → 即座に閾値以下
    var svc = service(2, 3);
    String token = svc.issue(1L, "user@example.com");
    var jwt = svc.decode(token);

    // Act & Assert
    assertThat(svc.needsRefresh(jwt)).isTrue();
  }

  @Test
  void testNeedsRefreshWhenRemainingAboveThreshold() {
    // Arrange — 有効期間 480 分、閾値 240 分 → まだ余裕あり
    var svc = service(480, 240);
    String token = svc.issue(1L, "user@example.com");
    var jwt = svc.decode(token);

    // Act & Assert
    assertThat(svc.needsRefresh(jwt)).isFalse();
  }

  @Test
  void testDecodeInvalidToken() {
    // Arrange
    var svc = service(480, 240);

    // Act & Assert
    assertThatThrownBy(() -> svc.decode("invalid.token.here")).isInstanceOf(JwtException.class);
  }

  @Test
  void testBuildAuthCookie() {
    // Arrange
    var svc = service(480, 240);
    String token = svc.issue(1L, "user@example.com");

    // Act
    var cookie = svc.buildAuthCookie(token);

    // Assert
    assertThat(cookie.getName()).isEqualTo("AUTH_TOKEN");
    assertThat(cookie.getValue()).isEqualTo(token);
    assertThat(cookie.isHttpOnly()).isTrue();
    assertThat(cookie.isSecure()).isFalse();
    assertThat(cookie.getSameSite()).isEqualTo("Strict");
    assertThat(cookie.getPath()).isEqualTo("/");
    assertThat(cookie.getMaxAge()).isEqualTo(java.time.Duration.ofMinutes(480));
  }

  @Test
  void testBuildClearAuthCookie() {
    // Arrange
    var svc = service(480, 240);

    // Act
    var cookie = svc.buildClearAuthCookie();

    // Assert
    assertThat(cookie.getName()).isEqualTo("AUTH_TOKEN");
    assertThat(cookie.getValue()).isEmpty();
    assertThat(cookie.getMaxAge()).isZero();
  }
}
