package com.example.bpa.modules.authorizationserver.bootstrap;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import com.example.bpa.modules.authorizationserver.service.Oauth2ClientRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Component
public class Oauth2ClientRegistrationBootstrap
    implements ApplicationListener<ApplicationReadyEvent> {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final Oauth2ClientRegistrationService oauth2ClientRegistrationService;

  public Oauth2ClientRegistrationBootstrap(
      Oauth2ClientRegistrationService oauth2ClientRegistrationService) {
    this.oauth2ClientRegistrationService = oauth2ClientRegistrationService;
  }

  private static Oauth2ClientRegistration getOauth2ClientRegistration(String name) {
    final Oauth2ClientRegistration oauth2ClientRegistrationDto = new Oauth2ClientRegistration();
    oauth2ClientRegistrationDto.setProviderName("keycloak");
    oauth2ClientRegistrationDto.setRegistrationId("keycloak");
    oauth2ClientRegistrationDto.setClientId("signature-profile");
    oauth2ClientRegistrationDto.setClientSecret("MSongX8Z7fjECCRMiigY938QzjqBBOA7");
    oauth2ClientRegistrationDto.setIssuerUri(
        "https://sig-testing.allweb.com.kh:8070/realms/signature-identification");
    oauth2ClientRegistrationDto.setProviderType("Oid");
    oauth2ClientRegistrationDto.setTenantId(name);
    return oauth2ClientRegistrationDto;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    try {
      final ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
      final String name = environment.getProperty("tenant.name", "master");
      final Oauth2ClientRegistration oauth2ClientRegistrationDto = getOauth2ClientRegistration(name);
      oauth2ClientRegistrationService.save(oauth2ClientRegistrationDto);
      logger.info("OAuth2 Client Registration Successfully Created");
    } catch (Exception e) {
      logger.warn("Failed to register Oauth2 client {}", e.getMessage());
      logger.error(e.getMessage());
    }
  }
}
