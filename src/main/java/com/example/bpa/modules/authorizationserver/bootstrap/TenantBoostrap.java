package com.example.bpa.modules.authorizationserver.bootstrap;

import com.example.bpa.modules.authorizationserver.dto.TenantDTO;
import com.example.bpa.modules.authorizationserver.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class TenantBoostrap implements ApplicationListener<ApplicationReadyEvent> {
  private final Logger logger = LoggerFactory.getLogger(TenantBoostrap.class);
  private final TenantService tenantService;

  public TenantBoostrap(TenantService tenantService) {
    this.tenantService = tenantService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    try {
      final ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
      final String name = environment.getProperty("tenant.name", "master");
      final String email = environment.getProperty("tenant.email", "master@example.com");
      final TenantDTO tenantDTO = new TenantDTO();
      tenantDTO.setName(name);
      tenantDTO.setEmail(email);
      final TenantDTO tenant = this.tenantService.createTenant(tenantDTO);
      logger.info("Default tenant created with ID: {}", tenant.getId());
    } catch (Exception e) {
      if (e instanceof DataIntegrityViolationException) {
        logger.error(
            "Failed to create default entities due to data integrity issue: {}", e.getMessage());
        throw new DuplicateKeyException("Bootstrap failed: Duplicate data detected", e);
      }
      logger.error("Unexpected error during bootstrap: {}", e.getMessage());
      throw new IllegalArgumentException("Bootstrap failed: " + e.getMessage(), e);
    }
  }
}
