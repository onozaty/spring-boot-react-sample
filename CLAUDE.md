# CLAUDE.md

## 修正完了時の手順

コードの修正が完了したら、必ず以下を順番に実行すること。backend / frontend の両方をまとめて対象とする集約タスクをルートプロジェクトに登録してある。

### 1. フォーマット

```bash
./gradlew format
```

### 2. 静的解析 (lint / typecheck / SpotBugs)

```bash
./gradlew lint
```

### 3. テスト

```bash
./gradlew test
```

### 4. E2E テスト (必要に応じて)

```bash
./gradlew e2eTest
```

## ライブラリ追加時の注意

ライブラリを追加する際は、以下を調査してから使用すること。

- **最新バージョン**: Maven Central の metadata や GitHub releases を確認する
- **メンテナンス状況**: GitHub のコミット・リリース頻度、issue 対応状況を確認し、放棄されていないことを確認する
- **利用実績**: ダウンロード数やスター数など、実際に使われているかを確認する
- **対象バージョンとの互換性**: 使用している Spring Boot / Java のバージョンと互換があることを確認する

## コーディング規約

### テストコード

テストは AAA（Arrange / Act / Assert）パターンで記述すること。

```java
@Test
void testExample() {
  // Arrange
  ...

  // Act
  ...

  // Assert
  ...
}
```

Arrange が不要な場合（異常系など）は `// Act & Assert` とまとめてよい。
