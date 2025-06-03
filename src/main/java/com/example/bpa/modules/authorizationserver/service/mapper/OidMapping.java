package com.example.bpa.modules.authorizationserver.service.mapper;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import java.util.Collections;
import java.util.Set;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;

@Configuration
public class OidMapping extends AbstractClientRegistrationMapping {

  @Override
  public Set<String> getTypes() {
    return Collections.singleton("Oid");
  }

  @Override
  protected ClientRegistration.Builder resolveBuilder(
      String registrationId, Oauth2ClientRegistration registrationDto) {
    return ClientRegistrations.fromIssuerLocation(registrationDto.getIssuerUri());
  }
}
