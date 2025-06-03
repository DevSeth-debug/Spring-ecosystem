package com.example.bpa.modules.authorizationserver.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
  private Cors cors = new Cors();
  private Oauth2 oauth2 = new Oauth2();

  @Data
  public static class Cors {
    private List<String> allowedOrigins = List.of("http://localhost:4200");
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    private List<String> allowedHeaders = List.of("*");
    private boolean allowCredentials = true;
  }

  @Data
  public static class Oauth2 {
    private boolean multipleIssuersAllowed = true;
    private String loginUrl = "/login";
    private String logoutUrl = "/logout";
  }
}
