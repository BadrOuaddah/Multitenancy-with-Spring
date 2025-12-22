package com.example.multitenancy_keycloak;

import com.example.multitenancy_keycloak.configure.TenantIssuerConfig;
import com.example.multitenancy_keycloak.product.ProductEntity;
import com.example.multitenancy_keycloak.product.ProductRepository;
import com.example.multitenancy_keycloak.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class MultitenancyKeycloakApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TenantIssuerConfig tenantIssuerConfig;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void cleanTenant(String tenant) {
        TenantContext.setTenant(tenant);
        productRepository.deleteAll();
        TenantContext.clear();
    }

    @AfterEach
    void cleanup() {
        cleanTenant("tenant1");
        cleanTenant("tenant2");
    }

    @Test
    void getIssuer() {
        assertThat(tenantIssuerConfig.getIssuer("tenant1"))
                .isEqualTo("http://localhost:8080/realms/tenant1");
        assertThat(tenantIssuerConfig.getIssuer("tenant2"))
                .isEqualTo("http://localhost:8080/realms/tenant2");
    }

    @Test
    void tenantOneConnect(){
        TenantContext.setTenant("tenant1");
        String url = jdbcTemplate.execute((ConnectionCallback<String>) (conn) -> conn.getMetaData().getURL());
        System.out.println("Tenant1 datasource URL: " + url);
        assertThat(url).contains("db_tenant1");

        ProductEntity product = new ProductEntity("Laptop");
        productRepository.save(product);

        List<ProductEntity> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Laptop");

        String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        assertThat(dbName).isEqualTo("db_tenant1");
        System.out.println("Tenant1 database connected: " + dbName);

    }

    @Test
    void tenantTwoConnect(){
        TenantContext.setTenant("tenant2");
        String url = jdbcTemplate.execute((ConnectionCallback<String>) (conn) -> conn.getMetaData().getURL());
        System.out.println("Tenant2 datasource URL: " + url);
        assertThat(url).contains("db_tenant2");

        ProductEntity product = new ProductEntity("Phone");
        productRepository.save(product);

        List<ProductEntity> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Phone");

        String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        assertThat(dbName).isEqualTo("db_tenant2");
        System.out.println("Tenant2 database connected: " + dbName);

    }

}
