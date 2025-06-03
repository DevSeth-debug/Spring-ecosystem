package com.example.bpa.modules.authorizationserver.service.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class TenantAwareOAuth2AuthorizationRequestResolver
    implements OAuth2AuthorizationRequestResolver {
  private static final Pattern URI_PATTERN =
      Pattern.compile(".*/([^/]+)/oauth2/(authorize|authorization|code)/([^/]+)$");
  private static final String DEFAULT_AUTHORIZATION_REQUEST_URI = "/oauth2/authorization";
  private final OAuth2AuthorizationRequestResolver defaultOAuth2RequestResolver;
  private final ClientRegistrationRepository clientRegistrationRepository;

  public TenantAwareOAuth2AuthorizationRequestResolver(
      ClientRegistrationRepository clientRegistrationRepository) {
    this.defaultOAuth2RequestResolver =
        new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, DEFAULT_AUTHORIZATION_REQUEST_URI);
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  /**
   * Returns the {@link OAuth2AuthorizationRequest} resolved from the provided {@code
   * HttpServletRequest} or {@code null} if not available.
   *
   * @param request the {@code HttpServletRequest}
   * @return the resolved {@link OAuth2AuthorizationRequest} or {@code null} if not available
   */
  @Override
  public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    String uri = request.getRequestURI();
    Matcher matcher = URI_PATTERN.matcher(uri);

    if (!matcher.find()) {
      return null; // fallback to default behavior (or return null)
    }

    String tenant = matcher.group(1); // e.g., "realm1"
    String registrationId = matcher.group(3); // e.g., "keycloak"

    // Save tenant to request attribute or thread context
    request.setAttribute("tenant", tenant); // Optional: use in filters, etc.

    // Rewrite URI to match what Default resolver expects
    HttpServletRequest wrappedRequest =
        new HttpServletRequestWrapper(request) {
          @Override
          public String getRequestURI() {
            return "/oauth2/authorization/" + registrationId;
          }
        };
    String contextPath = request.getContextPath();
    if (contextPath != null && StringUtils.equalsIgnoreCase(contextPath, "/" + tenant)) {
      return defaultOAuth2RequestResolver.resolve(wrappedRequest);
    }
    return new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, "/" + tenant + DEFAULT_AUTHORIZATION_REQUEST_URI)
        .resolve(wrappedRequest);
  }

  /**
   * Returns the {@link OAuth2AuthorizationRequest} resolved from the provided {@code
   * HttpServletRequest} or {@code null} if not available.
   *
   * @param request the {@code HttpServletRequest}
   * @param clientRegistrationId the clientRegistrationId to use
   * @return the resolved {@link OAuth2AuthorizationRequest} or {@code null} if not available
   */
  @Override
  public OAuth2AuthorizationRequest resolve(
      HttpServletRequest request, String clientRegistrationId) {
    return defaultOAuth2RequestResolver.resolve(request, clientRegistrationId);
  }
}
