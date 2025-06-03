package com.example.bpa.modules.authorizationserver.service;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import com.example.bpa.modules.authorizationserver.repository.Oauth2ClientRegistrationRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class Oauth2ClientRegistrationService {
  private final Oauth2ClientRegistrationRepository clientRegistrationRepository;

  public Oauth2ClientRegistrationService(
      Oauth2ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  @Transactional(rollbackOn = Exception.class)
  public Oauth2ClientRegistration save(Oauth2ClientRegistration registrationDto) {
    final Optional<Oauth2ClientRegistration> optionalOauth2ClientRegistration =
        clientRegistrationRepository.findByRegistrationId(registrationDto.getRegistrationId());
    return optionalOauth2ClientRegistration.orElseGet(
        () -> clientRegistrationRepository.save(registrationDto));
  }
}
