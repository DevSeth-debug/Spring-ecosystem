package com.example.bpa.modules.authorizationserver.repository;

import com.example.bpa.modules.authorizationserver.entity.Tenant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
  Optional<Tenant> findByNameAndEmail(String name, String email);

  boolean existsByName(String name);
}
