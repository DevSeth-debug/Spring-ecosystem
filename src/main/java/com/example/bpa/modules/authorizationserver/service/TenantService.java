package com.example.bpa.modules.authorizationserver.service;

import com.example.bpa.modules.authorizationserver.dto.TenantDTO;
import com.example.bpa.modules.authorizationserver.entity.Tenant;
import com.example.bpa.modules.authorizationserver.repository.TenantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TenantService {
  private final ObjectMapper objectMapper;
  private final TenantRepository tenantRepository;

  public TenantDTO getTenant(Long tenantId) {
    return tenantRepository
        .findById(tenantId)
        .map(tenant -> objectMapper.convertValue(tenant, TenantDTO.class))
        .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));
  }

  public boolean isValidTenant(String tenantName) {
    return tenantRepository.existsByName(tenantName);
  }

  public TenantDTO createTenant(TenantDTO tenant) {
    final Optional<Tenant> optionalTenant =
        tenantRepository.findByNameAndEmail(tenant.getName(), tenant.getEmail());
    if (optionalTenant.isPresent()) {
      return objectMapper.convertValue(optionalTenant.get(), TenantDTO.class);
    }
    Tenant newTenant = objectMapper.convertValue(tenant, Tenant.class);
    final Tenant save = tenantRepository.save(newTenant);
    return objectMapper.convertValue(save, TenantDTO.class);
  }
}
