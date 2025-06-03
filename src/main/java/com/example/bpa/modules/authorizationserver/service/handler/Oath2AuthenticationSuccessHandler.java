package com.example.bpa.modules.authorizationserver.service.handler;

import com.example.bpa.modules.authorizationserver.TenantContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

@Slf4j
public class Oath2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(Oath2AuthenticationSuccessHandler.class);
  private final SavedRequestAwareAuthenticationSuccessHandler delegate =
      new SavedRequestAwareAuthenticationSuccessHandler();
  private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    String currentTenant = TenantContext.getCurrentTenant();
    if (currentTenant == null || currentTenant.isEmpty()) {
      currentTenant = "master";
    }
    String redirectUrl = String.format("/%s/home", currentTenant);
    // Ensure SecurityContext is set
    SecurityContextHolder.getContext().setAuthentication(authentication);
    LOGGER.info("Session ID after login: {}", request.getSession().getId());
    LOGGER.info("SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
    LOGGER.info("Authentication type: {}", authentication.getClass().getSimpleName());
    // Try to retrieve saved OAuth2 request
    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
    if (authentication instanceof OAuth2AuthenticationToken
        || savedRequest != null && savedRequest.getRedirectUrl().contains("/oauth2/authorize")) {
      // For OAuth2, delegate to SavedRequestAwareAuthenticationSuccessHandler to respect
      // redirect_uri
      LOGGER.info(
          "Handling OAuth2 login, delegating to SavedRequestAwareAuthenticationSuccessHandler");
      this.delegate.setDefaultTargetUrl(redirectUrl);
      this.delegate.onAuthenticationSuccess(request, response, authentication);
      return;
    }
    // For form-based login, use direct redirect to tenant-specific home
    LOGGER.info("Handling form-based login, redirecting to: {}", redirectUrl);
    this.redirectStrategy.sendRedirect(request, response, redirectUrl);
  }
}
