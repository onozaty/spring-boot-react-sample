package com.github.onozaty.sample.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie")
public record CookieProperties(boolean secure) {}
