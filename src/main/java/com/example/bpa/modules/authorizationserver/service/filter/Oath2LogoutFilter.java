package com.example.bpa.modules.authorizationserver.service.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class Oath2LogoutFilter extends LogoutFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(Oath2LogoutFilter.class);

  public Oath2LogoutFilter(
      LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... logoutHandlers) {
    super(logoutSuccessHandler, logoutHandlers);
    // Match any /segment/logout endpoint (e.g., /master/logout, /signature/logout)
    RequestMatcher matcher = new RegexRequestMatcher("/.*/logout", "POST");
    setLogoutRequestMatcher(matcher);
  }

  @Override
  protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
    String requestPath = request.getRequestURI();
    String sessionId = request.getSession().getId();
    LOGGER.info("Checking logout for: {}, session: {}", requestPath, sessionId);
    return super.requiresLogout(request, response);
  }
}
