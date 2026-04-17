package com.github.onozaty.sample.config;

import static org.springframework.web.servlet.function.RequestPredicates.path;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class SpaForwardingConfig {

  @Bean
  RouterFunction<ServerResponse> spaRouter() {
    var index = new ClassPathResource("static/index.html");
    var staticResources = new ClassPathResource("static/");
    var spaPredicate = path("/api/**").or(path("/error")).negate();
    return route().resources("/**", staticResources).resource(spaPredicate, index).build();
  }
}
