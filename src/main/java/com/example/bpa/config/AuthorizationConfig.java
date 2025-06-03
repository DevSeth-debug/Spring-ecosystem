package com.example.bpa.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("authserver")
@Configuration
@ComponentScan(basePackages = "com.example.bpa.modules.authorizationserver")
public class AuthorizationConfig {}
