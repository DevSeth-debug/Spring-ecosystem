package com.example.bpa.modules.authorizationserver.repository;

import com.example.bpa.modules.authorizationserver.entity.Oauth2ClientRegistration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2ClientRegistrationRepository
    extends JpaRepository<Oauth2ClientRegistration, Long> {
  Optional<Oauth2ClientRegistration> findByRegistrationId(String registrationId);
}
