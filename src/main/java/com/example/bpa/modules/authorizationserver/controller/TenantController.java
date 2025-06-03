package com.example.bpa.modules.authorizationserver.controller;

import com.example.bpa.modules.authorizationserver.dto.TenantDTO;
import com.example.bpa.modules.authorizationserver.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {
  private final TenantService tenantService;

  public TenantController(TenantService tenantService) {
    this.tenantService = tenantService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<TenantDTO> getTenant(@PathVariable Long id) {

    return new ResponseEntity<>(tenantService.getTenant(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<HttpStatus> addTenant(TenantDTO tenant) {
    this.tenantService.createTenant(tenant);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
