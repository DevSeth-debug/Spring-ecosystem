package com.example.bpa;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerJwtAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(
    scanBasePackages = {
      "com.example.bpa.service",
      "com.example.bpa.repository",
      "com.example.bpa.controller",
      "com.example.bpa.repository",
      "com.example.bpa.config"
    },
    exclude = {OAuth2AuthorizationServerJwtAutoConfiguration.class})
public class BpaApplication {
  private final Logger log = LoggerFactory.getLogger(this.getClass());
  @Autowired private Environment env;

  public static void main(String[] args) {
    SpringApplication.run(BpaApplication.class, args);
  }

  @PostConstruct
  public void logActiveProfiles() {
    log.info("Active Profile :{}", env.getProperty("spring.profiles.active"));
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
