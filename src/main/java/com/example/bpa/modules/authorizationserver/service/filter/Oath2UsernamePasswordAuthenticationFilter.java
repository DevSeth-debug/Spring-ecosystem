package com.example.bpa.modules.authorizationserver.service.filter;

import com.example.bpa.modules.authorizationserver.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Slf4j
public class Oath2UsernamePasswordAuthenticationFilter
    extends UsernamePasswordAuthenticationFilter {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(Oath2UsernamePasswordAuthenticationFilter.class);

  public Oath2UsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
    setRequiresAuthenticationRequestMatcher(new RegexRequestMatcher("/.*/login", "POST"));
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    LOGGER.info("Session ID before authentication: {}", request.getSession().getId());
    LOGGER.info("Login tenant: {}", TenantContext.getCurrentTenant());
    Authentication auth = super.attemptAuthentication(request, response);
    LOGGER.info("Session ID after authentication: {}", request.getSession().getId());
    return auth;
  }
}
