package com.example.multitenancy_keycloak;

import com.example.multitenancy_keycloak.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class TenantContextTest {

    @Test
    void storeAndClearTenant(){
        TenantContext.setTenant("tenant1");

        assertEquals("tenant1", TenantContext.getTenant());

        TenantContext.clear();
        assertNull(TenantContext.getTenant());
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }
}
