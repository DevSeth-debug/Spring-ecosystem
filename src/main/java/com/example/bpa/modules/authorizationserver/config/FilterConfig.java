package com.example.bpa.modules.authorizationserver.config;

import com.example.bpa.modules.authorizationserver.service.filter.Oath2LogoutFilter;
import com.example.bpa.modules.authorizationserver.service.filter.Oath2UsernamePasswordAuthenticationFilter;
import com.example.bpa.modules.authorizationserver.service.handler.BasicAuthenticationFailureHandler;
import com.example.bpa.modules.authorizationserver.service.handler.Oath2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

  @Bean
  public Oath2LogoutFilter oauth2LogoutFilter(LogoutSuccessHandler logoutSuccessHandler) {
    return new Oath2LogoutFilter(logoutSuccessHandler, new SecurityContextLogoutHandler());
  }

  @Bean
  public Oath2UsernamePasswordAuthenticationFilter passwordAuthenticationFilter(
      AuthenticationManager authenticationManager,
      SecurityContextRepository securityContextRepository) {

    Oath2AuthenticationSuccessHandler successHandler = new Oath2AuthenticationSuccessHandler();
    BasicAuthenticationFailureHandler failureHandler = new BasicAuthenticationFailureHandler();

    Oath2UsernamePasswordAuthenticationFilter filter =
        new Oath2UsernamePasswordAuthenticationFilter(authenticationManager);
    filter.setAuthenticationSuccessHandler(successHandler);
    filter.setAuthenticationFailureHandler(failureHandler);
    filter.setSecurityContextRepository(securityContextRepository);

    return filter;
  }
}
