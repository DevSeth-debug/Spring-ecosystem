package com.example.bpa.modules.authorizationserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "oauth2_client_registration")
public class Oauth2ClientRegistration {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String registrationId;
  private String clientId;
  private String clientSecret;
  private String redirectUri;
  private String scopes;
  private String providerName;
  private String issuerUri;
  private String providerType;
  private String tenantId;
}
