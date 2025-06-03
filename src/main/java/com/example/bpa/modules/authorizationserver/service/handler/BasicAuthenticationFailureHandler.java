package com.example.bpa.modules.authorizationserver.service.handler;

import com.example.bpa.modules.authorizationserver.TenantContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class BasicAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    AuthenticationFailureHandler badCredentialsHandler =
        new SimpleUrlAuthenticationFailureHandler(resolveUrl("bad credentials"));
    AuthenticationFailureHandler lockedAccountHandler =
        new SimpleUrlAuthenticationFailureHandler(resolveUrl("locked"));
    AuthenticationFailureHandler defaultHandler =
        new SimpleUrlAuthenticationFailureHandler(resolveUrl("generic"));
    AuthenticationFailureHandler expiredAccountHandler =
        new SimpleUrlAuthenticationFailureHandler(resolveUrl("expired"));
    // Create a map of exceptions to handlers
    LinkedHashMap<Class<? extends AuthenticationException>, AuthenticationFailureHandler> handlers =
        new LinkedHashMap<>();
    handlers.put(BadCredentialsException.class, badCredentialsHandler);
    handlers.put(LockedException.class, lockedAccountHandler);
    handlers.put(AccountExpiredException.class, expiredAccountHandler);
    new DelegatingAuthenticationFailureHandler(handlers, defaultHandler)
        .onAuthenticationFailure(request, response, exception);
  }

  private String resolveUrl(String status) {
    final String currentTenant = TenantContext.getCurrentTenant();
    return String.format("/%s/login?error=%s", currentTenant, status);
  }
}
