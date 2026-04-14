package com.github.onozaty.sample.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "バリデーションエラーレスポンス")
public record ValidationProblemDetail(
    @Schema(description = "エラータイトル", example = "Bad Request") String title,
    @Schema(description = "HTTPステータスコード", example = "400") int status,
    @Schema(description = "エラー詳細", example = "Invalid request content.") String detail,
    @Schema(description = "リクエストパス", example = "/api/users") String instance,
    @Schema(description = "フィールドエラー一覧") List<FieldError> errors) {

  @Schema(description = "フィールドエラー")
  public record FieldError(
      @Schema(description = "フィールド名", example = "name") String field,
      @Schema(description = "エラーメッセージ", example = "must not be blank") String message) {}
}
