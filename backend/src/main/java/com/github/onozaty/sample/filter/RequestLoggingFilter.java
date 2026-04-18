package com.github.onozaty.sample.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var method = request.getMethod();
    var uri = request.getRequestURI();
    var query = request.getQueryString();
    var path = query != null ? uri + "?" + query : uri;

    logger.info("Incoming: {} {}", method, path);
    var startTime = System.currentTimeMillis();
    try {
      filterChain.doFilter(request, response);
    } finally {
      var elapsed = System.currentTimeMillis() - startTime;
      logger.info("Outgoing: {} {} {} ({}ms)", method, path, response.getStatus(), elapsed);
    }
  }
}
