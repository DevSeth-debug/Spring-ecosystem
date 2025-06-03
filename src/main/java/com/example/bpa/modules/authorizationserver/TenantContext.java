package com.example.bpa.modules.authorizationserver;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantContext {
  private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

  public static String getCurrentTenant() {
    return currentTenant.get();
  }

  public static void setCurrentTenant(String tenant) {
    currentTenant.set(tenant);
  }

  public static void removeCurrentTenant() {
    currentTenant.remove();
  }
}
