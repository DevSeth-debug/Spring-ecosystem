package com.example.bpa.modules.authorizationserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantDTO {
  private Long id;
  private String name;
  private String email;
}
