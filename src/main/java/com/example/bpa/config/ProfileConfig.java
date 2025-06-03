package com.example.bpa.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("profile")
@Configuration
@ComponentScan(basePackages = "com.example.bpa.profile")
public class ProfileConfig {
}
