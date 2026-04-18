# spring-boot-react-sample

Spring Boot と React を組み合わせた Web アプリケーションのサンプルプロジェクトです。
ユーザー管理機能（一覧・登録・編集・削除）を実装しています。

## 構成

```
spring-boot-react-sample/
├── backend/          # Spring Boot バックエンド (Java 25)
├── frontend/         # React フロントエンド (TypeScript + Vite)
└── .devcontainer/    # Dev Container 設定
```

マルチプロジェクト構成の Gradle でビルドを管理しています。
開発環境は Dev Container で提供しており、PostgreSQL データベースと pgAdmin4 も含まれています。

## 技術スタック

### バックエンド

| カテゴリ         | ライブラリ / フレームワーク                                                                                           |
| ---------------- | --------------------------------------------------------------------------------------------------------------------- |
| フレームワーク   | [Spring Boot](https://spring.io/projects/spring-boot) 4.0                                                             |
| 言語             | Java 25                                                                                                               |
| DB アクセス      | [MyBatis](https://mybatis.org/) 4.0                                                                                   |
| マイグレーション | [Flyway](https://flywaydb.org/)                                                                                       |
| API ドキュメント | [springdoc-openapi](https://springdoc.org/) 3.0                                                                       |
| データベース     | [PostgreSQL](https://www.postgresql.org/) 17                                                                          |
| フォーマット     | [Spotless](https://github.com/diffplug/spotless) + [google-java-format](https://github.com/google/google-java-format) |
| 静的解析         | [SpotBugs](https://spotbugs.github.io/)                                                                               |
| カバレッジ       | [JaCoCo](https://www.jacoco.org/jacoco/)                                                                              |
| ビルドツール     | [Gradle](https://gradle.org/)                                                                                         |

### フロントエンド

| カテゴリ             | ライブラリ / フレームワーク                                                                           |
| -------------------- | ----------------------------------------------------------------------------------------------------- |
| フレームワーク       | [React](https://react.dev/) 19                                                                        |
| 言語                 | TypeScript 6                                                                                          |
| ビルドツール         | [Vite](https://vite.dev/) 8                                                                           |
| ルーティング         | [TanStack Router](https://tanstack.com/router)                                                        |
| データフェッチ       | [TanStack Query](https://tanstack.com/query) + [openapi-fetch](https://openapi-ts.dev/openapi-fetch/) |
| UI コンポーネント    | [shadcn/ui](https://ui.shadcn.com/) ([Radix UI](https://www.radix-ui.com/) ベース)                    |
| スタイリング         | [Tailwind CSS](https://tailwindcss.com/) 4                                                            |
| Lint                 | [ESLint](https://eslint.org/)                                                                         |
| フォーマット         | [Prettier](https://prettier.io/)                                                                      |
| パッケージマネージャ | [pnpm](https://pnpm.io/)                                                                              |

## 動作環境

Dev Container を使用するため、以下が必要です。

- [Docker](https://www.docker.com/)
- [VS Code](https://code.visualstudio.com/) + [Dev Containers 拡張機能](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

## 起動方法

### 1. Dev Container の起動

VS Code でリポジトリを開き、コマンドパレットから **Dev Containers: Reopen in Container** を実行します。

コンテナ起動後、pgAdmin4 が利用可能になります。

| サービス | URL                   |
| -------- | --------------------- |
| pgAdmin4 | http://localhost:5050 |

### 2. バックエンドの起動

```bash
./gradlew :backend:bootRun
```

起動後、以下の URL でアクセスできます。

| サービス         | URL                                   |
| ---------------- | ------------------------------------- |
| バックエンド API | http://localhost:8080                 |
| Swagger UI       | http://localhost:8080/swagger-ui.html |

### 3. フロントエンドの起動

初回はパッケージのインストールが必要です。

```bash
# Gradle から実行する場合
./gradlew :frontend:pnpmInstall

# pnpm から直接実行する場合
cd frontend
pnpm install
```

続いて、バックエンドを起動した状態で API クライアントを生成します。

```bash
# Gradle から実行する場合
./gradlew :frontend:generate

# pnpm から直接実行する場合
cd frontend
pnpm generate
```

その後、開発サーバーを起動します。

```bash
# Gradle から実行する場合
./gradlew :frontend:dev

# pnpm から直接実行する場合
cd frontend
pnpm dev
```

起動後、http://localhost:5173 でアクセスできます。

## ビルド

### 実行可能 JAR / WAR（フロントエンド込み）

```bash
# JAR
./gradlew :backend:bootJar

# WAR
./gradlew :backend:bootWar
```

フロントエンドのビルド → パッケージングが自動で実行されます。
生成先: `backend/build/libs/backend-0.0.1-SNAPSHOT.jar` または `.war`

```bash
# JAR で起動
java -jar backend/build/libs/backend-0.0.1-SNAPSHOT.jar
```

### バックエンド

```bash
# ビルド
./gradlew :backend:build

# テスト
./gradlew :backend:test

# フォーマット
./gradlew :backend:spotlessApply

# 静的解析
./gradlew :backend:spotbugsMain :backend:spotbugsTest
```

### フロントエンド

```bash
# 型チェック
./gradlew :frontend:typecheck
# または: cd frontend && pnpm typecheck

# Lint
./gradlew :frontend:lint
# または: cd frontend && pnpm lint

# フォーマット
./gradlew :frontend:format
# または: cd frontend && pnpm format

# プロダクションビルド
./gradlew :frontend:build
# または: cd frontend && pnpm build
```

## API クライアントの生成

バックエンドを起動した状態で以下を実行すると、OpenAPI スキーマから TypeScript の型定義を生成します。

```bash
cd frontend
pnpm generate
```

生成されたファイルは `frontend/src/generated/api.d.ts` に出力されます。

## 今後の実装予定

- フロントエンドのテスト
- バックエンドのログ出力
- フロントエンドのバリデーション
