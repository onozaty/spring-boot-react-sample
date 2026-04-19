package com.github.onozaty.sample;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

/** Spring Boot 統合テスト用のメタアノテーション。test プロファイル + DB リセット + SpringBootTest をまとめる */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(DatabaseResetExtension.class)
public @interface AppTest {

  @AliasFor(annotation = SpringBootTest.class, attribute = "webEnvironment")
  WebEnvironment webEnvironment() default WebEnvironment.MOCK;
}
