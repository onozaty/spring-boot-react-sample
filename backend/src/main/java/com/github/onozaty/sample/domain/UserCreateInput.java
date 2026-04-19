package com.github.onozaty.sample.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "ユーザー作成入力")
public class UserCreateInput {

  @NotBlank
  @Schema(description = "名前", requiredMode = Schema.RequiredMode.REQUIRED, example = "山田太郎")
  private String name;

  @NotBlank
  @Email
  @Schema(
      description = "メールアドレス",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "yamada@example.com")
  private String email;

  @NotBlank
  @Size(min = 8)
  @Schema(description = "パスワード（8文字以上）", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
