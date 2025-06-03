package com.example.bpa.modules.authorizationserver.config;

import com.example.bpa.modules.authorizationserver.service.TenantPerIssuerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

@Configuration(proxyBeanMethods = false)
public class OAuth2AuthorizationConsentServiceConfig {
  @Value("${tenant.name}")
  private String defaultTenant;

  @Bean
  public OAuth2AuthorizationConsentService authorizationConsentService(
      TenantPerIssuerRegistry componentRegistry,
      RegisteredClientRepository registeredClientRepository,
      JdbcTemplate jdbcTemplate) {

    componentRegistry.register(
        defaultTenant,
        OAuth2AuthorizationConsentService.class,
        new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository));

    return new DelegatingOAuth2AuthorizationConsentService(componentRegistry);
  }

  private record DelegatingOAuth2AuthorizationConsentService(
      TenantPerIssuerRegistry componentRegistry) implements OAuth2AuthorizationConsentService {

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
      getAuthorizationConsentService().save(authorizationConsent);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
      getAuthorizationConsentService().remove(authorizationConsent);
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
      return getAuthorizationConsentService().findById(registeredClientId, principalName);
    }

    private OAuth2AuthorizationConsentService getAuthorizationConsentService() {
      OAuth2AuthorizationConsentService authorizationConsentService =
          this.componentRegistry.get(OAuth2AuthorizationConsentService.class);
      Assert.state(
          authorizationConsentService != null,
          "OAuth2AuthorizationConsentService not found for \"requested\" issuer identifier.");
      return authorizationConsentService;
    }
  }
}
