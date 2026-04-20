package com.github.onozaty.sample.controller;

import com.github.onozaty.sample.domain.User;
import com.github.onozaty.sample.mapper.UserMapper;
import com.github.onozaty.sample.service.AuthService;
import com.github.onozaty.sample.service.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "認証API")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenService jwtTokenService;
  private final UserMapper userMapper;
  private final AuthService authService;

  public AuthController(
      AuthenticationManager authenticationManager,
      JwtTokenService jwtTokenService,
      UserMapper userMapper,
      AuthService authService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenService = jwtTokenService;
    this.userMapper = userMapper;
    this.authService = authService;
  }

  @PostMapping("/login")
  @Operation(summary = "ログイン", description = "メールアドレスとパスワードで認証し、JWT Cookie を発行します")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "ログイン成功"),
    @ApiResponse(responseCode = "401", description = "認証失敗")
  })
  public ResponseEntity<User> login(@Valid @RequestBody LoginRequest request) {
    var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
    var authentication = authenticationManager.authenticate(authToken);

    var email = authentication.getName();
    var user =
        userMapper
            .findByEmailWithCredential(email)
            .orElseThrow(
                () ->
                    new AuthenticationCredentialsNotFoundException("Authenticated user not found"));

    String token = jwtTokenService.issue(user.getId(), user.getEmail());

    var responseUser = new User();
    responseUser.setId(user.getId());
    responseUser.setName(user.getName());
    responseUser.setEmail(user.getEmail());

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtTokenService.buildAuthCookie(token).toString())
        .body(responseUser);
  }

  @PostMapping("/logout")
  @Operation(summary = "ログアウト", description = "JWT Cookie を削除します")
  @ApiResponse(responseCode = "204", description = "ログアウト成功")
  public ResponseEntity<Void> logout() {
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, jwtTokenService.buildClearAuthCookie().toString())
        .build();
  }

  @GetMapping("/me")
  @Operation(summary = "ログインユーザー取得", description = "現在ログイン中のユーザー情報を返します")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "取得成功"),
    @ApiResponse(responseCode = "401", description = "未認証")
  })
  public ResponseEntity<User> me(@AuthenticationPrincipal Jwt jwt) {
    Long userId = jwt.getClaim(JwtTokenService.CLAIM_USER_ID);
    var user =
        userMapper
            .findById(userId)
            .orElseThrow(
                () ->
                    new AuthenticationCredentialsNotFoundException("Authenticated user not found"));
    return ResponseEntity.ok(user);
  }

  @PatchMapping("/me/password")
  @Operation(summary = "パスワード変更", description = "ログイン中ユーザーのパスワードを変更します")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "変更成功"),
    @ApiResponse(responseCode = "400", description = "現在のパスワードが正しくない")
  })
  public ResponseEntity<Void> changePassword(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PasswordChangeRequest request) {
    Long userId = jwt.getClaim(JwtTokenService.CLAIM_USER_ID);
    authService.changePassword(userId, request.currentPassword(), request.newPassword());
    return ResponseEntity.noContent().build();
  }
}
