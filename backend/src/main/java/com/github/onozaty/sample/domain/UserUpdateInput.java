package com.github.onozaty.sample.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "ユーザー更新入力（パスワード変更は別エンドポイント）")
public class UserUpdateInput {

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
}
