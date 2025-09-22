package com.unibague.gradework.orionuser.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service-auth")
public class ServiceAuthProperties {
  private String token;
  private boolean allowInternalHeader = true;
  private String adminRole = "ROLE_ADMIN";

  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }

  public boolean isAllowInternalHeader() { return allowInternalHeader; }
  public void setAllowInternalHeader(boolean allowInternalHeader) { this.allowInternalHeader = allowInternalHeader; }

  public String getAdminRole() { return adminRole; }
  public void setAdminRole(String adminRole) { this.adminRole = adminRole; }
}
