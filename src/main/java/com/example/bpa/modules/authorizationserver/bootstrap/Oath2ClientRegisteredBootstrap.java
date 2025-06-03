package com.example.bpa.modules.authorizationserver.bootstrap;

import com.example.bpa.modules.authorizationserver.TenantContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

@Component
public class Oath2ClientRegisteredBootstrap implements ApplicationListener<ApplicationReadyEvent> {
  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    var repository = event.getApplicationContext().getBean(RegisteredClientRepository.class);
//    repository.save(getRegisteredClientRepository());
  }

  private RegisteredClient getRegisteredClientRepository() {

    final ClientSettings clientSettings =
        ClientSettings.builder().requireProofKey(true).requireAuthorizationConsent(true).build();
    return RegisteredClient.withId("angular-spa")
        .clientId("angular-spa")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // Public client
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("http://localhost:4200") // Angular app redirect URI
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope(OidcScopes.EMAIL)
        .clientSettings(clientSettings) // Enforce PKCE
        .build();
  }
}
