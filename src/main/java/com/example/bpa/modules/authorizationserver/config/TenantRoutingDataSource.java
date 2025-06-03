package com.example.bpa.modules.authorizationserver.config;

import com.example.bpa.modules.authorizationserver.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

  /**
   * Determine the current lookup key. This will typically be implemented to check a thread-bound
   * transaction context.
   *
   * <p>Allows for arbitrary keys. The returned key needs to match the stored lookup key type, as
   * resolved by the {@link #resolveSpecifiedLookupKey} method.
   */
  @Override
  protected Object determineCurrentLookupKey() {
    return TenantContext.getCurrentTenant();
  }
}
