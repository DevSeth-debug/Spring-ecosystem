package com.example.bpa.modules.authorizationserver.config;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import com.example.bpa.modules.authorizationserver.repository.Oauth2ClientRegistrationRepository;
import com.example.bpa.modules.authorizationserver.service.mapper.ClientRegistrationMapper;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class Oauth2ClientRegistrationRepositoryConfig {
  @Bean
  public ClientRegistrationRepository clientRegistrationRepository(
      Oauth2ClientRegistrationRepository registrationRepository,
      ClientRegistrationMapper clientRegistrationMapper) {
    return new DelegatingClientRegistrationRepository(
        registrationRepository, clientRegistrationMapper);
  }

  private static class DelegatingClientRegistrationRepository
      implements ClientRegistrationRepository, Iterable<ClientRegistration> {
    private final Map<String, ClientRegistration> clientRegistrationCaches =
        new ConcurrentHashMap<>();
    private final Oauth2ClientRegistrationRepository registrationRepository;
    private final ClientRegistrationMapper clientRegistrationMapper;

    public DelegatingClientRegistrationRepository(
        Oauth2ClientRegistrationRepository registrationRepository,
        ClientRegistrationMapper clientRegistrationMapper) {
      this.registrationRepository = registrationRepository;
      this.clientRegistrationMapper = clientRegistrationMapper;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
      ClientRegistration clientRegistration = clientRegistrationCaches.get(registrationId);
      if (clientRegistration != null) {
        return clientRegistration;
      }
      final Optional<ClientRegistration> registrationOptional =
          this.registrationRepository.findByRegistrationId(registrationId).map(this::builder);
      if (registrationOptional.isEmpty()) {
        return null;
      }
      clientRegistration = registrationOptional.get();
      clientRegistrationCaches.put(registrationId, clientRegistration);
      return clientRegistration;
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
      return this.registrationRepository.findAll().stream().map(this::builder).iterator();
    }

    private ClientRegistration builder(Oauth2ClientRegistration entity) {
      final ClientRegistration clientRegistration =
          clientRegistrationMapper.toClientRegistration(entity);
      return ClientRegistration.withClientRegistration(clientRegistration)
          .clientId(entity.getClientId())
          .clientSecret(entity.getClientSecret())
          .clientName(entity.getProviderName())
          .build();
    }
  }
}
