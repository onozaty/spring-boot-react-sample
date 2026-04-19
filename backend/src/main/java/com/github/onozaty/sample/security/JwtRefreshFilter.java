package com.github.onozaty.sample.security;

import com.github.onozaty.sample.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRefreshFilter extends OncePerRequestFilter {

  private final JwtTokenService jwtTokenService;

  public JwtRefreshFilter(JwtTokenService jwtTokenService) {
    this.jwtTokenService = jwtTokenService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // ダウンストリーム実行前に更新要否を判定し、必要なら Set-Cookie を先に積む。
    // doFilter 後に addHeader すると response がコミット済みの場合にヘッダが反映されない。
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      if (jwtTokenService.needsRefresh(jwt)) {
        Long userId = jwt.getClaim(JwtTokenService.CLAIM_USER_ID);
        String username = jwt.getSubject();
        String newToken = jwtTokenService.issue(userId, username);
        response.addHeader(
            HttpHeaders.SET_COOKIE, jwtTokenService.buildAuthCookie(newToken).toString());
      }
    }

    filterChain.doFilter(request, response);
  }
}
