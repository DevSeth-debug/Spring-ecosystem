package com.example.bpa.modules.authorizationserver.service.mapper;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.Set;

public interface ClientRegistrationMapping {
  Set<String> getTypes();

  ClientRegistration buildClientRegistration(Oauth2ClientRegistration registrationDto);
}
