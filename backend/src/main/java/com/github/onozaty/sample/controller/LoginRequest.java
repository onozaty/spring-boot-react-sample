package com.github.onozaty.sample.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "ログインリクエスト")
public record LoginRequest(
    @NotBlank
        @Email
        @Schema(
            description = "メールアドレス",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "admin@example.com")
        String email,
    @NotBlank @Schema(description = "パスワード", requiredMode = Schema.RequiredMode.REQUIRED)
        String password) {}
