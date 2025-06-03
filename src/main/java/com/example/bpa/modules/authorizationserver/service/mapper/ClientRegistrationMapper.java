package com.example.bpa.modules.authorizationserver.service.mapper;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class ClientRegistrationMapper implements ApplicationContextAware, InitializingBean {
  private ObjectProvider<ClientRegistrationMapping> mappingProvider;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (mappingProvider == null) {
      throw new IllegalArgumentException("ClientRegistrationMapping must not be null");
    }
  }

  public final ClientRegistration toClientRegistration(
      Oauth2ClientRegistration oauth2ClientRegistration) {
    validateDto(oauth2ClientRegistration);
    return buildClientRegistration(oauth2ClientRegistration);
  }

  protected void validateDto(Oauth2ClientRegistration clientRegistration) {
    if (clientRegistration.getClientId() == null || clientRegistration.getClientSecret() == null) {
      throw new IllegalArgumentException("clientId and clientSecret are required");
    }
  }

  protected ClientRegistration buildClientRegistration(
      Oauth2ClientRegistration oauth2ClientRegistration) {
    final String clientIssuer = oauth2ClientRegistration.getIssuerUri();
    final String clientProviderType = oauth2ClientRegistration.getProviderType();
    return mappingProvider.stream()
        .filter(
            beanMapping ->
                beanMapping.getTypes().contains(clientProviderType)
                    || fallbackPredicate(clientIssuer))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Unsupported common provider for "
                        + clientProviderType
                        + " or issuerUri "
                        + clientIssuer))
        .buildClientRegistration(oauth2ClientRegistration);
  }

  private boolean fallbackPredicate(String issuerUri) {
    final String defaultDomainIssuer = "google.com,facebook.com,github.com,okta.com";

    if (!StringUtils.hasText(issuerUri)) {
      return false;
    }

    try {
      URI uri = new URI(issuerUri);
      String host = uri.getHost(); // e.g., accounts.google.com

      if (host == null) return false;

      // Check if any of the allowed domains is contained in the host
      return Arrays.stream(defaultDomainIssuer.split(","))
          .anyMatch(host::endsWith); // e.g., endsWith("google.com")
    } catch (URISyntaxException e) {
      return false;
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.mappingProvider = applicationContext.getBeanProvider(ClientRegistrationMapping.class);
  }
}
