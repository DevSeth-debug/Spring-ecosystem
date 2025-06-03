package com.example.bpa.modules.authorizationserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class WellKnownConfig {

  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  @Order(0)
  public SecurityFilterChain blockDefaultWellKnown(HttpSecurity http) throws Exception {
    http.securityMatcher(new AntPathRequestMatcher("/.well-known/**"))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/*/.well-known/openid-configuration")
                    .permitAll()
                    .anyRequest()
                    .denyAll());
    http.cors(cors -> cors.configurationSource(corsConfigurationSource));
    return http.build();
  }
}
