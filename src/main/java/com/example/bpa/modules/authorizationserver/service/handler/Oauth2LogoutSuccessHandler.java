package com.example.bpa.modules.authorizationserver.service.handler;

import com.example.bpa.modules.authorizationserver.TenantContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class Oauth2LogoutSuccessHandler implements LogoutSuccessHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(Oauth2LogoutSuccessHandler.class);
  private final ClientRegistrationRepository clientRegistrationRepository;

  public Oauth2LogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  @Override
  public void onLogoutSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    String tenant = TenantContext.getCurrentTenant();
    if (tenant == null || tenant.isEmpty()) {
      tenant = "master"; // Fallback tenant
    }
    String redirectUrl = String.format("/%s/login", tenant);
    String sessionId = request.getSession().getId();
    LOGGER.info("Logout for session: {}, tenant: {}", sessionId, tenant);

    if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
      // Handle OAuth2 (Keycloak) logout
      OidcClientInitiatedLogoutSuccessHandler oidcLogoutHandler =
          new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
      oidcLogoutHandler.setPostLogoutRedirectUri("{baseUrl}" + redirectUrl);
      oidcLogoutHandler.onLogoutSuccess(request, response, authentication);
      return;
    }

    // Handle form-based logout
    LOGGER.info("Redirecting to: {} after form-based logout", redirectUrl);
    response.sendRedirect(request.getContextPath() + redirectUrl);
  }
}
