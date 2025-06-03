package com.example.bpa.modules.authorizationserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2ClientRegistrationDto implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String clientName;
  private String clientId;
  private String clientSecret;
  private String issuerUri;
  private String providerType; // e.g., "GOOGLE", "GITHUB", "CUSTOM"

  @JsonProperty(access = Access.READ_ONLY)
  private String redirectUri;

  private String registrationId;
  private String providerName;
}
