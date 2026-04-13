package com.github.onozaty.sample.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "ユーザー")
public class User {

  @Schema(description = "ユーザーID", accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "名前", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "メールアドレス", requiredMode = Schema.RequiredMode.REQUIRED)
  private String email;

  @Schema(description = "作成日時", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime createdAt;

  @Schema(description = "更新日時", accessMode = Schema.AccessMode.READ_ONLY)
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
