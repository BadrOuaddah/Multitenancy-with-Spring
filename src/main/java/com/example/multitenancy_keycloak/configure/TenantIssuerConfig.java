package com.example.multitenancy_keycloak.configure;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TenantIssuerConfig {
    private final Map<String, String> issuers = Map.of(
            "tenant1", "http://localhost:8080/realms/tenant1",
            "tenant2", "http://localhost:8080/realms/tenant2"
    );

    public String getIssuer(String tenant) {
        return issuers.get(tenant);
    }
}
