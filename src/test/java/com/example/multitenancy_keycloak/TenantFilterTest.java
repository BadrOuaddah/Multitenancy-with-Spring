package com.example.multitenancy_keycloak;

import com.example.multitenancy_keycloak.tenant.TenantContext;
import com.example.multitenancy_keycloak.tenant.TenantFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TenantFilterTest {

    TenantFilter tenantFilter = new TenantFilter();

    @Test
    void doFilterInternalTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Tenant-ID", "tenant1");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (req, res) -> {
            assertEquals("tenant1", TenantContext.getTenant());
        };

        tenantFilter.doFilter(request, response, filterChain);

        assertNull(TenantContext.getTenant());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void doFilterInternalErrorTest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        tenantFilter.doFilter(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("X-Tenant-ID header is required", response.getContentAsString());
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(TenantContext.getTenant());
    }

}
