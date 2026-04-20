package com.github.onozaty.sample;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseResetExtension implements BeforeEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    var dataSource = SpringExtension.getApplicationContext(context).getBean(DataSource.class);
    var jdbcClient = JdbcClient.create(dataSource);

    List<String> tables =
        jdbcClient
            .sql(
                """
                SELECT quote_ident(tablename) FROM pg_tables
                WHERE schemaname = 'public'
                AND tablename NOT LIKE 'flyway_%'
                """)
            .query(String.class)
            .list();

    if (!tables.isEmpty()) {
      jdbcClient
          .sql("TRUNCATE TABLE " + String.join(", ", tables) + " RESTART IDENTITY CASCADE")
          .update();
    }

    // 各テストで使うデフォルトユーザ（admin）を投入する
    jdbcClient
        .sql(
            """
            INSERT INTO users (name, email, created_at, updated_at)
            VALUES ('admin', 'admin@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
        .update();
    jdbcClient
        .sql(
            """
            INSERT INTO user_credentials (user_id, password_hash, created_at, updated_at)
            SELECT id, '$2a$10$pcUixkg46sBY9IxlYdz1UemalgvlwEkUi5T7hofcqVY3DdZsknzvO', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            FROM users WHERE email = 'admin@example.com'
            """)
        .update();
  }
}
