package com.example.bpa.modules.authorizationserver.service.mapper;

import com.example.bpa.modules.authorizationserver.dto.Oauth2ClientRegistrationDto;
import java.util.Set;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;

@Component
public class CommonProviderMapping extends AbstractClientRegistrationMapping {

  @Override
  public Set<String> getTypes() {
    return Set.of("GOOGLE", "GITHUB", "FACEBOOK", "OKTA");
  }

  @Override
  protected ClientRegistration.Builder resolveBuilder(
      String registrationId, Oauth2ClientRegistration registrationDto) {
    final CommonOAuth2Provider commonProvider =
        getCommonProvider(registrationDto.getProviderType(), registrationDto.getIssuerUri());
    if (commonProvider != null) {
      return commonProvider.getBuilder(registrationId);
    }
    throw new IllegalArgumentException(
        "Unsupported common provider for "
            + registrationDto.getProviderType()
            + " or issuerUri "
            + registrationDto.getIssuerUri());
  }

  private CommonOAuth2Provider getCommonProvider(String providerType, String issuerUri) {
    if (providerType != null) {
      return switch (providerType.toUpperCase()) {
        case "GOOGLE" -> CommonOAuth2Provider.GOOGLE;
        case "GITHUB" -> CommonOAuth2Provider.GITHUB;
        case "FACEBOOK" -> CommonOAuth2Provider.FACEBOOK;
        case "OKTA" -> CommonOAuth2Provider.OKTA;
        default -> null;
      };
    } else if (issuerUri != null) {
      if (issuerUri.contains("google.com")) {
        return CommonOAuth2Provider.GOOGLE;
      } else if (issuerUri.contains("github.com")) {
        return CommonOAuth2Provider.GITHUB;
      } else if (issuerUri.contains("facebook.com")) {
        return CommonOAuth2Provider.FACEBOOK;
      } else if (issuerUri.contains("okta.com")) {
        return CommonOAuth2Provider.OKTA;
      }
    }
    return null;
  }
}
