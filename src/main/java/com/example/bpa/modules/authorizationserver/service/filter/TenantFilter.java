package com.example.bpa.modules.authorizationserver.service.filter;

import com.example.bpa.modules.authorizationserver.TenantContext;
import com.example.bpa.modules.authorizationserver.service.TenantService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@AllArgsConstructor
public class TenantFilter extends OncePerRequestFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(TenantFilter.class);
  private static final String TENANT_ID_ATTRIBUTE = "tenantId";
  private final TenantService tenantService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String sessionId = request.getSession().getId();
    try {
      String requestUri = request.getRequestURI();
      String tenantId = extractTenantId(requestUri);
      if (tenantId.isEmpty()) {
        LOGGER.warn("No tenant ID found for request: {}, session: {}", requestUri, sessionId);
        throw new IllegalArgumentException("Tenant ID is required in the URL");
      }
      if (!tenantId.matches("[a-zA-Z0-9]+")) {
        LOGGER.warn(
            "Invalid tenant ID format: {} for request: {}, session: {}",
            tenantId,
            requestUri,
            sessionId);
        throw new IllegalArgumentException("Tenant ID must be alphanumeric");
      }
      if (!tenantService.isValidTenant(tenantId)) {
        LOGGER.warn(
            "Invalid tenant ID: {} for request: {}, session: {}", tenantId, requestUri, sessionId);
        throw new IllegalArgumentException("Invalid tenant ID: " + tenantId);
      }

      TenantContext.setCurrentTenant(tenantId);
      request.setAttribute(TENANT_ID_ATTRIBUTE, tenantId);
      LOGGER.debug(
          "Set tenant ID: [{}] for request: {}, session: {}", tenantId, requestUri, sessionId);

      filterChain.doFilter(request, response);
    } catch (IllegalArgumentException ex) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
    } finally {
      TenantContext.removeCurrentTenant();
      LOGGER.debug(
          "Cleared tenant context for request: {}, session: {}",
          request.getRequestURI(),
          sessionId);
    }
  }

  private String extractTenantId(String requestUri) {
    // Expect URLs like /realm/{tenant}/...
    String[] segments = requestUri.split("/");
    if (segments.length > 2 && "realm".equals(segments[1])) {
      return segments[2];
    }
    return "";
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    // Allow tenant filtering for OAuth2 authorization endpoints
    // Only skip for truly tenant-agnostic endpoints
    return path.startsWith("/health")
        || path.startsWith("/actuator")
        || path.startsWith("/realm/css/")
        || path.startsWith("/realm/images/")
        || (path.startsWith("/realm/login/oauth2/code/keycloak")
            && !path.contains("/oauth2/authorize"));
    // Removed general OAuth2 exclusion to ensure tenant context is set for authorization requests
  }
}
