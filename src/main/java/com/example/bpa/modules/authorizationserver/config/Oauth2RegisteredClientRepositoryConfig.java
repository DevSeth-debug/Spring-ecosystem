package com.example.bpa.modules.authorizationserver.config;

import com.example.bpa.modules.authorizationserver.service.TenantPerIssuerRegistry;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;

@Configuration(proxyBeanMethods = false)
public class Oauth2RegisteredClientRepositoryConfig {
  @Value("${tenant.name}")
  private String defaultTenant;

  @Bean
  public RegisteredClientRepository registeredClientRepository(
      JdbcTemplate jdbcTemplate,
      TenantPerIssuerRegistry registry,
      PasswordEncoder bCryptPasswordEncoder) {
    final JdbcRegisteredClientRepository jdbcRegisteredClientRepository =
        new JdbcRegisteredClientRepository(jdbcTemplate);
    jdbcRegisteredClientRepository.save(getRegisteredClientRepository());
    registry.register(
        defaultTenant, RegisteredClientRepository.class, jdbcRegisteredClientRepository);
    return new DelegatingRegisteredClientRepository(registry);
  }

  public RegisteredClient registeredClientNextJs(PasswordEncoder bCryptPasswordEncoder) {
    return RegisteredClient.withId("6c736b05-97d7-44ba-ac5f-a9b497797e09")
            .clientId("next-js-client")
            .clientSecret(bCryptPasswordEncoder.encode("root"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://localhost:3000/api/auth/callback/spring")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope(OidcScopes.EMAIL)
            .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .build())
            .build();
  }
  private RegisteredClient getRegisteredClientRepository(PasswordEncoder bCryptPasswordEncoder) {
    return RegisteredClient.withId("6c736b05-97d7-44ba-ac5f-a9b497797e08")
        .clientId("ang-client")
        .clientSecret(bCryptPasswordEncoder.encode("root"))
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantTypes(
            authorizationGrantTypes -> {
              authorizationGrantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
              authorizationGrantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
              authorizationGrantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
            })
        .redirectUris(
            urIs -> {
              urIs.add("http://localhost:4200/dashboard");
              urIs.add("http://localhost:4200/login");
              urIs.add("http://localhost:3000");
              urIs.add("http://localhost:3000/api/auth/callback");
            })
        .scope("openid")
        .scope("profile")
        .scope("email")
        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
        .build();
  }

  private RegisteredClient getRegisteredClientRepository() {

    final ClientSettings clientSettings =
        ClientSettings.builder()
            .requireProofKey(true) // 🔐 Require PKCE
            .requireAuthorizationConsent(true)
            .build();

    final TokenSettings tokenSettings =
        TokenSettings.builder()
            .accessTokenTimeToLive(Duration.ofMinutes(30))
            .refreshTokenTimeToLive(Duration.ofMinutes(20))
            .build();
    return RegisteredClient.withId("public-client")
        .clientId("public-client")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE) // Public client
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .redirectUri("http://localhost:4200/callback") // Angular app redirect URI
        .redirectUri("http://localhost:4200/login")
        .redirectUri("http://localhost:4200")
        .redirectUri("http://localhost:3000/api/auth/callback/oauth-provider")
        .redirectUri("http://localhost:3000/api/auth/callback/spring")
        .redirectUri("http://localhost:3000/api/auth/callback/Bp-Auth")
        .redirectUri("http://localhost:3000/api/auth/callback/oidc-provider")
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope(OidcScopes.EMAIL)
        .clientSettings(clientSettings) // Enforce PKCE
        .tokenSettings(tokenSettings)
        .build();
  }

  private record DelegatingRegisteredClientRepository(TenantPerIssuerRegistry componentRegistry)
      implements RegisteredClientRepository {

    @Override
    public void save(RegisteredClient registeredClient) {
      getRegisteredClientRepository().save(registeredClient);
    }

    @Override
    public RegisteredClient findById(String id) {
      return getRegisteredClientRepository().findById(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
      return getRegisteredClientRepository().findByClientId(clientId);
    }

    private RegisteredClientRepository getRegisteredClientRepository() {
      RegisteredClientRepository registeredClientRepository =
          this.componentRegistry.get(RegisteredClientRepository.class);
      Assert.state(
          registeredClientRepository != null,
          "RegisteredClientRepository not found for \"requested\" issuer identifier.");
      return registeredClientRepository;
    }
  }
}
