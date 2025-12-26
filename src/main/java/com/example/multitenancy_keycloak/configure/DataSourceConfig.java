package com.example.multitenancy_keycloak.configure;

import com.example.multitenancy_keycloak.tenant.TenantContext;
import com.example.multitenancy_keycloak.tenant.TenantIdHasher;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    private final TenantIdHasher tenantIdHasher;

    public DataSourceConfig(TenantIdHasher tenantIdHasher) {
        this.tenantIdHasher = tenantIdHasher;
    }

    @Bean
    public DataSource dataSource() {

        Map<Object, Object> targetDataSources = new HashMap<>();

        targetDataSources.put(tenantIdHasher.hashTenantId("tenant1"), createDataSource("db_tenant1"));
        targetDataSources.put(tenantIdHasher.hashTenantId("tenant2"), createDataSource("db_tenant2"));

        AbstractRoutingDataSource routingDataSource =
                new AbstractRoutingDataSource() {
                    @Override
                    protected Object determineCurrentLookupKey() {
                        String tenant = TenantContext.getTenant();
                        return tenantIdHasher.hashTenantId(tenant);
                    }
                };

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(
                createDataSource("db_tenant1")
        );

        return routingDataSource;
    }

    private DataSource createDataSource(String database) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/" + database);
        ds.setUsername("postgres");
        ds.setPassword("postgres");
        return ds;
    }
}
