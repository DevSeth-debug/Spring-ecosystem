package com.example.bpa.modules.authorizationserver.service.handler;

import com.example.bpa.modules.authorizationserver.TenantContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
  private static final String DEFAULT_URL = "/error";
  private final SimpleUrlAuthenticationFailureHandler failureHandler =
      new SimpleUrlAuthenticationFailureHandler();

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    final String currentTenant = TenantContext.getCurrentTenant();
    final String errorUri =
        (currentTenant == null) ? DEFAULT_URL : "/" + currentTenant + DEFAULT_URL;
    failureHandler.setDefaultFailureUrl(errorUri);
    failureHandler.onAuthenticationFailure(request, response, exception);
  }
}
