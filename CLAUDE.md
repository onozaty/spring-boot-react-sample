# CLAUDE.md

## 修正完了時の手順

コードの修正が完了したら、必ず以下を順番に実行すること。

### 1. フォーマット

```bash
./gradlew :backend:spotlessApply
```

### 2. テスト

```bash
./gradlew :backend:test
```

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
