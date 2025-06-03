package com.example.bpa.modules.authorizationserver.service.mapper;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.CollectionUtils;

public abstract class AbstractClientRegistrationMapping
    implements ClientRegistrationMapping, ApplicationContextAware {

  private ClientRegistrationConfig config;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.config = applicationContext.getBean(ClientRegistrationConfig.class);
  }

  @Override
  public ClientRegistration buildClientRegistration(Oauth2ClientRegistration registrationDto) {
    String registrationId = deriveRegistrationId(registrationDto);
    final ClientRegistration.Builder builder = resolveBuilder(registrationId, registrationDto);
    final ClientRegistration clientRegistration =
        builder
            .registrationId(registrationId)
            .clientId(registrationDto.getClientId())
            .clientSecret(registrationDto.getClientSecret())
            .redirectUri(config.getDefaultRedirectUri())
            .clientName(
                registrationDto.getProviderName() != null
                    ? registrationDto.getProviderName()
                    : registrationId)
            .build();
    final ClientRegistration.Builder fallbackBuilder =
        ClientRegistration.withClientRegistration(clientRegistration);
    if (CollectionUtils.isEmpty(clientRegistration.getScopes())) {
      fallbackBuilder.scope(config.getScopes());
    }
    return fallbackBuilder.build();
  }

  protected String deriveRegistrationId(Oauth2ClientRegistration registrationDto) {
    if (registrationDto.getProviderName() != null) {
      return registrationDto.getProviderName().toLowerCase().replaceAll("[^a-z0-9]", "-");
    } else if (registrationDto.getProviderType() != null) {
      return registrationDto.getProviderType().toLowerCase();
    } else if (registrationDto.getIssuerUri() != null) {
      return registrationDto.getIssuerUri().replaceAll("https?://|[:/.]", "-").toLowerCase();
    }
    throw new IllegalArgumentException("clientName, providerType, or issuerUri must be provided");
  }

  protected abstract ClientRegistration.Builder resolveBuilder(
      String registrationId, Oauth2ClientRegistration registrationDto);
}
