package com.example.bpa.modules.authorizationserver.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {
  public static final String LOGIN_ALL = "/*/login";
  public static final String OAUTH2_URL = "/oauth2/**";
  public static final String LOGIN_URL = "/login";
  public static final String LOGOUT_URL = "/logout";

  public static final String[] STATIC_RESOURCES = {
    "/css/**", "/images/**", "/webjars/**", "/favicon.ico"
  };

  public static final String[] PUBLIC_REQUESTS = {
    LOGIN_URL,
    LOGOUT_URL,
    OAUTH2_URL,
    LOGIN_ALL,
    "/*/logout",
    "/*/oauth2/authorize/**",
    "/*/oauth2/authorization/**"
  };
}
