package com.example.bpa.modules.authorizationserver.entrypoint;

import com.example.bpa.modules.authorizationserver.TenantContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class TenantAwareAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TenantAwareAuthenticationEntryPoint.class);

  public TenantAwareAuthenticationEntryPoint(String loginFormUrl) {
    super(loginFormUrl);
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    // Extract tenant from the original request
    String tenantId = extractTenantFromRequest(request);

    LOGGER.debug("Original request URI: {}", request.getRequestURI());
    LOGGER.debug("Request parameters: {}", request.getParameterMap().keySet());
    LOGGER.debug(
        "Request headers: Authorization={}, X-Tenant-ID={}",
        request.getHeader("Authorization"),
        request.getHeader("X-Tenant-ID"));

    // If tenant context is null, try to extract from request
    if (TenantContext.getCurrentTenant() == null && tenantId != null) {
      LOGGER.debug("Setting tenant context from request: {}", tenantId);
      TenantContext.setCurrentTenant(tenantId);
    }

    String currentTenant = TenantContext.getCurrentTenant();
    LOGGER.debug("Current tenant in AuthenticationEntryPoint: {}", currentTenant);

    if (currentTenant != null) {
      // Build tenant-aware login URL
      String tenantAwareLoginUrl = buildTenantAwareLoginUrl(request, currentTenant);
      LOGGER.debug("Redirecting to tenant-aware login URL: {}", tenantAwareLoginUrl);

      // Create a custom LoginUrlAuthenticationEntryPoint with the tenant-aware URL
      LoginUrlAuthenticationEntryPoint tenantEntryPoint =
          new LoginUrlAuthenticationEntryPoint(tenantAwareLoginUrl);
      tenantEntryPoint.commence(request, response, authException);
    } else {
      // Fallback: extract tenant from request URL and redirect
      if (tenantId != null) {
        String fallbackLoginUrl = "/" + tenantId + "/login";
        LOGGER.debug("Using fallback login URL: {}", fallbackLoginUrl);
        response.sendRedirect(fallbackLoginUrl);
      } else {
        // If no tenant can be determined, return error
        LOGGER.warn("No tenant context available for request: {}", request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response
            .getWriter()
            .write(
                "{\"error\": \"tenant_required\", \"message\": \"Tenant information is required\"}");
      }
    }
  }

  private String extractTenantFromRequest(HttpServletRequest request) {
    // Method 1: Extract from URL path /realm/{tenant}/...
    String requestUri = request.getRequestURI();
    String[] segments = requestUri.split("/");
    if (segments.length > 2 && "realm".equals(segments[1])) {
      return segments[2];
    }

    // Method 2: Extract from request parameter
    String tenantParam = request.getParameter("tenant");
    if (tenantParam != null && !tenantParam.isEmpty()) {
      return tenantParam;
    }

    // Method 3: Extract from custom header
    String tenantHeader = request.getHeader("X-Tenant-ID");
    if (tenantHeader != null && !tenantHeader.isEmpty()) {
      return tenantHeader;
    }

    // Method 4: Extract from subdomain
    String serverName = request.getServerName();
    if (serverName.contains(".")) {
      String subdomain = serverName.split("\\.")[0];
      if (!subdomain.equals("www") && !subdomain.equals("api")) {
        return subdomain;
      }
    }

    // Method 5: Extract from referer header (for OAuth2 flows)
    String referer = request.getHeader("Referer");
    if (referer != null) {
      String tenantFromReferer = extractTenantFromUrl(referer);
      if (tenantFromReferer != null) {
        return tenantFromReferer;
      }
    }

    return null;
  }

  private String extractTenantFromUrl(String url) {
    try {
      // Extract tenant from URL like http://localhost:8080/realm/tenant1/...
      String[] parts = url.split("/");
      for (int i = 0; i < parts.length - 1; i++) {
        if ("realm".equals(parts[i]) && i + 1 < parts.length) {
          return parts[i + 1];
        }
      }
    } catch (Exception e) {
      LOGGER.debug("Could not extract tenant from URL: {}", url);
    }
    return null;
  }

  private String buildTenantAwareLoginUrl(HttpServletRequest request, String tenantId) {
    // Build the tenant-aware login URL
    String baseLoginUrl = "/" + tenantId + "/login";

    // Preserve important OAuth2 parameters if present
    StringBuilder loginUrl = new StringBuilder(baseLoginUrl);

    // Add return URL parameter if this is an OAuth2 authorization request
    String requestUri = request.getRequestURI();
    String queryString = request.getQueryString();

    if (requestUri.contains("/oauth2/authorize") && queryString != null) {
      // Store the original OAuth2 request for after login
      String originalRequest = requestUri + "?" + queryString;
      loginUrl
          .append("?continue=")
          .append(
              java.net.URLEncoder.encode(originalRequest, java.nio.charset.StandardCharsets.UTF_8));
    }

    return loginUrl.toString();
  }

  @Override
  protected String buildRedirectUrlToLoginPage(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) {
    // This method is called by the parent class
    String tenantId = TenantContext.getCurrentTenant();
    if (tenantId == null) {
      tenantId = extractTenantFromRequest(request);
    }

    if (tenantId != null) {
      return buildTenantAwareLoginUrl(request, tenantId);
    }

    return super.buildRedirectUrlToLoginPage(request, response, authException);
  }
}
