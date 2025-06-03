package com.example.bpa.modules.authorizationserver.config;

import static com.example.bpa.modules.authorizationserver.constant.SecurityConstants.LOGIN_URL;

import com.example.bpa.modules.authorizationserver.entrypoint.TenantAwareAuthenticationEntryPoint;
import com.example.bpa.modules.authorizationserver.service.filter.TenantFilter;
import com.example.bpa.modules.authorizationserver.service.handler.OAuth2AuthenticationFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class OAuth2AuthorizationServerConfig {

  private final TenantFilter tenantFilter;
  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(
      HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {

    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        new OAuth2AuthorizationServerConfigurer();
    final RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

    http.securityMatcher(endpointsMatcher)
        .with(
            authorizationServerConfigurer,
            configurer ->
                configurer
                    .oidc(Customizer.withDefaults())
                    .authorizationEndpoint(
                        authorizationEndpoint ->
                            authorizationEndpoint.errorResponseHandler(
                                new OAuth2AuthenticationFailureHandler())))
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
        .exceptionHandling(
            exceptions ->
                exceptions.defaultAuthenticationEntryPointFor(
                    new TenantAwareAuthenticationEntryPoint(LOGIN_URL),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
        .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .securityContext(security -> security.securityContextRepository(securityContextRepository));

    http.addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().multipleIssuersAllowed(true).build();
  }
}
