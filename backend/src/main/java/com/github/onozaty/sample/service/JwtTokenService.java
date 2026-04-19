package com.github.onozaty.sample.service;

import com.github.onozaty.sample.config.CookieProperties;
import com.github.onozaty.sample.config.JwtProperties;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

  public static final String COOKIE_NAME = "AUTH_TOKEN";
  public static final String CLAIM_USER_ID = "uid";

  private final JwtEncoder encoder;
  private final JwtDecoder decoder;
  private final JwtProperties jwtProperties;
  private final CookieProperties cookieProperties;

  public JwtTokenService(JwtProperties jwtProperties, CookieProperties cookieProperties) {
    this.jwtProperties = jwtProperties;
    this.cookieProperties = cookieProperties;

    var key =
        new SecretKeySpec(jwtProperties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    this.encoder =
        new NimbusJwtEncoder(new com.nimbusds.jose.jwk.source.ImmutableSecret<>(key.getEncoded()));
    this.decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  }

  public String issue(Long userId, String username) {
    var now = Instant.now();
    var claims =
        JwtClaimsSet.builder()
            .subject(username)
            .claim(CLAIM_USER_ID, userId)
            .issuedAt(now)
            .expiresAt(now.plus(jwtProperties.expirationMinutes(), ChronoUnit.MINUTES))
            .build();
    return encoder
        .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
        .getTokenValue();
  }

  public Jwt decode(String token) {
    return decoder.decode(token);
  }

  public JwtDecoder jwtDecoder() {
    return decoder;
  }

  /** JWT の残り有効時間が refresh-threshold-minutes 未満かどうかを判定する */
  public boolean needsRefresh(Jwt jwt) {
    var exp = jwt.getExpiresAt();
    if (exp == null) {
      return false;
    }
    long remainingMinutes = ChronoUnit.MINUTES.between(Instant.now(), exp);
    return remainingMinutes < jwtProperties.refreshThresholdMinutes();
  }

  /** 認証 Cookie を組み立てる */
  public ResponseCookie buildAuthCookie(String token) {
    return ResponseCookie.from(COOKIE_NAME, token)
        .httpOnly(true)
        .secure(cookieProperties.secure())
        .sameSite("Strict")
        .path("/")
        .maxAge(Duration.ofMinutes(jwtProperties.expirationMinutes()))
        .build();
  }

  /** ログアウト用（Cookie を即時失効させる）Cookie を組み立てる */
  public ResponseCookie buildClearAuthCookie() {
    return ResponseCookie.from(COOKIE_NAME, "")
        .httpOnly(true)
        .secure(cookieProperties.secure())
        .sameSite("Strict")
        .path("/")
        .maxAge(0)
        .build();
  }
}
