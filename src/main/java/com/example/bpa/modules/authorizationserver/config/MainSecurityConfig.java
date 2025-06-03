package com.example.bpa.modules.authorizationserver.config;

import static com.example.bpa.modules.authorizationserver.constant.SecurityConstants.LOGIN_ALL;
import static com.example.bpa.modules.authorizationserver.constant.SecurityConstants.LOGIN_URL;
import static com.example.bpa.modules.authorizationserver.constant.SecurityConstants.LOGOUT_URL;
import static com.example.bpa.modules.authorizationserver.constant.SecurityConstants.OAUTH2_URL;
import static com.example.bpa.modules.authorizationserver.constant.SecurityConstants.PUBLIC_REQUESTS;
import static com.example.bpa.modules.authorizationserver.constant.SecurityConstants.STATIC_RESOURCES;

import com.example.bpa.modules.authorizationserver.entrypoint.TenantAwareAuthenticationEntryPoint;
import com.example.bpa.modules.authorizationserver.service.filter.Oath2LogoutFilter;
import com.example.bpa.modules.authorizationserver.service.filter.Oath2UsernamePasswordAuthenticationFilter;
import com.example.bpa.modules.authorizationserver.service.filter.TenantFilter;
import com.example.bpa.modules.authorizationserver.service.handler.BasicAuthenticationFailureHandler;
import com.example.bpa.modules.authorizationserver.service.handler.OAuth2AuthenticationFailureHandler;
import com.example.bpa.modules.authorizationserver.service.handler.Oath2AuthenticationSuccessHandler;
import com.example.bpa.modules.authorizationserver.service.resolver.TenantAwareOAuth2AuthorizationRequestResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class MainSecurityConfig {

  private final TenantFilter tenantFilter;
  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(
      HttpSecurity http,
      LogoutSuccessHandler logoutSuccessHandler,
      TenantAwareOAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver,
      SecurityContextRepository securityContextRepository,
      Oath2LogoutFilter logoutFilter,
      Oath2UsernamePasswordAuthenticationFilter passwordAuthenticationFilter)
      throws Exception {

    Oath2AuthenticationSuccessHandler successHandler = new Oath2AuthenticationSuccessHandler();
    BasicAuthenticationFailureHandler failureHandler = new BasicAuthenticationFailureHandler();

    return http.authorizeHttpRequests(this::configureAuthorization)
        .formLogin(form -> configureFormLogin(form, successHandler, failureHandler))
        .oauth2Login(
            oauth2 ->
                configureOAuth2Login(oauth2, oauth2AuthorizationRequestResolver, successHandler))
        .logout(logout -> configureLogout(logout, logoutSuccessHandler))
        .csrf(this::configureCsrf)
        .exceptionHandling(this::configureExceptionHandling)
        .sessionManagement(this::configureSessionManagement)
        .headers(Customizer.withDefaults())
        .securityContext(security -> security.securityContextRepository(securityContextRepository))
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(passwordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(logoutFilter, LogoutFilter.class)
        .build();
  }

  private void configureAuthorization(
      AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry authorize) {
    authorize
        .requestMatchers(STATIC_RESOURCES)
        .permitAll()
        .requestMatchers(PUBLIC_REQUESTS)
        .permitAll()
        .requestMatchers("/realm/*/home")
        .authenticated()
        .anyRequest()
        .authenticated();
  }

  private void configureFormLogin(
      FormLoginConfigurer<?> form,
      Oath2AuthenticationSuccessHandler successHandler,
      BasicAuthenticationFailureHandler failureHandler) {
    form.loginPage(LOGIN_ALL)
        .loginProcessingUrl(LOGIN_ALL)
        .successHandler(successHandler)
        .failureHandler(failureHandler)
        .permitAll();
  }

  private void configureOAuth2Login(
      OAuth2LoginConfigurer<?> oauth2,
      TenantAwareOAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver,
      Oath2AuthenticationSuccessHandler successHandler) {
    oauth2
        .authorizationEndpoint(
            endpoint -> endpoint.authorizationRequestResolver(oauth2AuthorizationRequestResolver))
        .loginPage(LOGIN_ALL)
        .successHandler(successHandler)
        .failureHandler(new OAuth2AuthenticationFailureHandler())
        .permitAll();
  }

  private void configureLogout(
      LogoutConfigurer<?> logout, LogoutSuccessHandler logoutSuccessHandler) {
    logout
        .logoutUrl("/*/logout")
        .logoutSuccessHandler(logoutSuccessHandler)
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .deleteCookies("JSESSIONID")
        .permitAll();
  }

  private void configureCsrf(CsrfConfigurer<?> csrf) {
    csrf.ignoringRequestMatchers(LOGOUT_URL, OAUTH2_URL)
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
  }

  private void configureExceptionHandling(ExceptionHandlingConfigurer<?> exceptions) {
    exceptions.defaultAuthenticationEntryPointFor(
        new TenantAwareAuthenticationEntryPoint(LOGIN_URL),
        new MediaTypeRequestMatcher(MediaType.TEXT_HTML));
  }

  private void configureSessionManagement(SessionManagementConfigurer<?> session) {
    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
  }
}
