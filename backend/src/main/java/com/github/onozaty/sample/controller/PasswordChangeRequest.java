package com.github.onozaty.sample.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "パスワード変更リクエスト")
public record PasswordChangeRequest(
    @NotBlank @Schema(description = "現在のパスワード", requiredMode = Schema.RequiredMode.REQUIRED)
        String currentPassword,
    @NotBlank
        @Size(min = 8)
        @Schema(description = "新しいパスワード（8文字以上）", requiredMode = Schema.RequiredMode.REQUIRED)
        String newPassword) {}
