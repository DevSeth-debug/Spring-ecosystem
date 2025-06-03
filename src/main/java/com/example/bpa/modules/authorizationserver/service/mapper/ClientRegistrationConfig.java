package com.example.bpa.modules.authorizationserver.service.mapper;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth2.client")
public class ClientRegistrationConfig {
  private Set<String> scopes = Set.of("openid", "profile", "email");
  private String defaultRedirectUri = "{baseUrl}/login/oauth2/code/{registrationId}";
}
