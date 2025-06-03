//package com.example.bpa.modules.authorizationserver.config;
//
//import com.example.bpa.modules.authorizationserver.entrypoint.TenantAwareAuthenticationEntryPoint;
//import com.example.bpa.modules.authorizationserver.service.filter.Oath2LogoutFilter;
//import com.example.bpa.modules.authorizationserver.service.filter.Oath2UsernamePasswordAuthenticationFilter;
//import com.example.bpa.modules.authorizationserver.service.filter.TenantFilter;
//import com.example.bpa.modules.authorizationserver.service.handler.BasicAuthenticationFailureHandler;
//import com.example.bpa.modules.authorizationserver.service.handler.OAuth2AuthenticationFailureHandler;
//import com.example.bpa.modules.authorizationserver.service.handler.Oath2AuthenticationSuccessHandler;
//import com.example.bpa.modules.authorizationserver.service.resolver.TenantAwareOAuth2AuthorizationRequestResolver;
//import java.util.Arrays;
//import java.util.List;
//
//import com.nimbusds.jose.jwk.source.JWKSource;
//import com.nimbusds.jose.proc.SecurityContext;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
//import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
//import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.authentication.logout.LogoutFilter;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//import org.springframework.security.web.context.SecurityContextRepository;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//  public static final String LOGIN_ALL = "/*/login";
//  private static final String OAUTH2_URL = "/oauth2/**";
//  private static final String LOGIN_URL = "/login";
//  private static final String LOGOUT_URL = "/logout";
//  private static final String[] STATIC_RESOURCES = {
//    "/css/**", "/images/**", "/webjars/**", "/favicon.ico"
//  };
//  private static final String[] PUBLIC_REQUESTS = {
//    LOGIN_URL,
//    LOGOUT_URL,
//    OAUTH2_URL,
//    LOGIN_ALL,
//    "/*/logout",
//    "/*/oauth2/authorize/**",
//    "/*/oauth2/authorization/**"
//  };
//
//  private final TenantFilter tenantFilter;
//
//  @Bean
//  public PasswordEncoder passwordEncoder() {
//    return new BCryptPasswordEncoder();
//  }
//
//  @Bean
//  public SecurityContextRepository securityContextRepository() {
//    return new HttpSessionSecurityContextRepository();
//  }
//
//  @Bean
//  @Order(0)
//  public SecurityFilterChain blockDefaultWellKnown(HttpSecurity http) throws Exception {
//    http.securityMatcher(new AntPathRequestMatcher("/.well-known/**"))
//        .authorizeHttpRequests(
//            auth ->
//                auth.requestMatchers("/*/.well-known/openid-configuration")
//                    .permitAll()
//                    .anyRequest()
//                    .denyAll());
//    http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
//    return http.build();
//  }
//
//  @Bean
//  @Order(1)
//  public SecurityFilterChain authorizationServerSecurityFilterChain(
//      HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
//    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
//        new OAuth2AuthorizationServerConfigurer();
//    final RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
//    http.securityMatcher(endpointsMatcher)
//        .with(
//            authorizationServerConfigurer,
//            configurer ->
//                configurer
//                    .oidc(Customizer.withDefaults())
//                    .authorizationEndpoint(
//                        authorizationEndpoint ->
//                            authorizationEndpoint.errorResponseHandler(
//                                new OAuth2AuthenticationFailureHandler()) // Apply custom
//                        // failure handler
//                        ))
//        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
//        .exceptionHandling(
//            exceptions ->
//                exceptions.defaultAuthenticationEntryPointFor(
//                    new TenantAwareAuthenticationEntryPoint(LOGIN_URL),
//                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
//        .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
//        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//        .securityContext(security -> security.securityContextRepository(securityContextRepository));
//    http.addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class);
//
//    return http.build();
//  }
//
//  @Bean
//  @Order(2)
//  public SecurityFilterChain defaultSecurityFilterChain(
//      HttpSecurity http,
//      LogoutSuccessHandler logoutSuccessHandler,
//      TenantAwareOAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver,
//      AuthenticationManager authenticationManager,
//      SecurityContextRepository securityContextRepository)
//      throws Exception {
//    Oath2LogoutFilter logoutFilter =
//        new Oath2LogoutFilter(
//            logoutSuccessHandler,
//            new SecurityContextLogoutHandler()); // Default logout handler to clear SecurityContext
//    Oath2AuthenticationSuccessHandler successHandler = new Oath2AuthenticationSuccessHandler();
//    BasicAuthenticationFailureHandler failureHandler = new BasicAuthenticationFailureHandler();
//    Oath2UsernamePasswordAuthenticationFilter passwordAuthenticationFilter =
//        new Oath2UsernamePasswordAuthenticationFilter(authenticationManager);
//    passwordAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
//    passwordAuthenticationFilter.setAuthenticationFailureHandler(failureHandler);
//    passwordAuthenticationFilter.setSecurityContextRepository(securityContextRepository);
//    http.authorizeHttpRequests(
//            authorize ->
//                authorize
//                    .requestMatchers(STATIC_RESOURCES)
//                    .permitAll()
//                    .requestMatchers(PUBLIC_REQUESTS)
//                    .permitAll()
//                    .requestMatchers("/realm/*/home")
//                    .authenticated()
//                    .anyRequest()
//                    .authenticated())
//        .formLogin(
//            form ->
//                form.loginPage(LOGIN_ALL)
//                    .loginProcessingUrl(LOGIN_ALL)
//                    .successHandler(successHandler)
//                    .failureHandler(failureHandler)
//                    .permitAll())
//        .oauth2Login(
//            oauth2 ->
//                oauth2
//                    .authorizationEndpoint(
//                        endpoint ->
//                            endpoint.authorizationRequestResolver(
//                                oauth2AuthorizationRequestResolver))
//                    .loginPage(LOGIN_ALL)
//                    .successHandler(successHandler)
//                    .failureHandler(new OAuth2AuthenticationFailureHandler())
//                    .permitAll())
//        .logout(
//            logout ->
//                logout
//                    .logoutUrl("/*/logout")
//                    .logoutSuccessHandler(logoutSuccessHandler)
//                    .invalidateHttpSession(true)
//                    .clearAuthentication(true)
//                    .deleteCookies("JSESSIONID")
//                    .permitAll())
//        .csrf(
//            csrf ->
//                csrf.ignoringRequestMatchers(LOGOUT_URL, OAUTH2_URL)
//                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
//        .exceptionHandling(
//            exceptions ->
//                exceptions.defaultAuthenticationEntryPointFor(
//                    new TenantAwareAuthenticationEntryPoint(LOGIN_URL),
//                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
//        .sessionManagement(
//            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//        .headers(Customizer.withDefaults())
//        .securityContext(security -> security.securityContextRepository(securityContextRepository))
//        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//        .addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class)
//        .addFilterBefore(passwordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//        .addFilterBefore(logoutFilter, LogoutFilter.class);
//    http.cors(Customizer.withDefaults()); // enable CORS
//    return http.build();
//  }
//
//  @Bean
//  public AuthorizationServerSettings authorizationServerSettings() {
//    return AuthorizationServerSettings.builder().multipleIssuersAllowed(true).build();
//  }
//
///*  @Bean
//  public AuthenticationManager authenticationManager(
//      AuthenticationConfiguration authenticationConfiguration) throws Exception {
//    return authenticationConfiguration.getAuthenticationManager();
//  }
//
//  @Bean
//  public AuthenticationProvider authenticationProvider(
//      PasswordEncoder passwordEncoder,
//      @Qualifier("oauth2UserDetailsService") UserDetailsService userDetailsService) {
//    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//    authProvider.setUserDetailsService(userDetailsService);
//    authProvider.setPasswordEncoder(passwordEncoder);
//    return authProvider;
//  }*/
//
//  @Bean
//  public CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.setAllowedOriginPatterns(List.of("http://localhost:4200"));
//    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//    configuration.setAllowedHeaders(List.of("*"));
//    configuration.setAllowCredentials(true);
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", configuration);
//    return source;
//  }
//}
