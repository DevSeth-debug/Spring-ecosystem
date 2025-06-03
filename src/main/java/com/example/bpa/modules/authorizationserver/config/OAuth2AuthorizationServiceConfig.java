package com.example.bpa.modules.authorizationserver.config;

import com.example.bpa.modules.authorizationserver.service.TenantPerIssuerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

@Configuration(proxyBeanMethods = false)
public class OAuth2AuthorizationServiceConfig {
  @Value("${tenant.name}")
  private String defaultTenant;

  @Bean
  public OAuth2AuthorizationService authorizationService(
      TenantPerIssuerRegistry componentRegistry,
      RegisteredClientRepository registeredClientRepository,
      JdbcTemplate jdbcTemplate) {

    componentRegistry.register(
        defaultTenant,
        OAuth2AuthorizationService.class,
        new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository));

    return new DelegatingOAuth2AuthorizationService(componentRegistry);
  }

  private record DelegatingOAuth2AuthorizationService(TenantPerIssuerRegistry componentRegistry)
      implements OAuth2AuthorizationService {

    @Override
    public void save(OAuth2Authorization authorization) {
      getAuthorizationService().save(authorization);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
      getAuthorizationService().remove(authorization);
    }

    @Override
    public OAuth2Authorization findById(String id) {
      return getAuthorizationService().findById(id);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
      return getAuthorizationService().findByToken(token, tokenType);
    }

    private OAuth2AuthorizationService getAuthorizationService() {
      OAuth2AuthorizationService authorizationService =
          this.componentRegistry.get(OAuth2AuthorizationService.class);
      Assert.state(
          authorizationService != null,
          "OAuth2AuthorizationService not found for \"requested\" issuer identifier.");
      return authorizationService;
    }
  }
}
