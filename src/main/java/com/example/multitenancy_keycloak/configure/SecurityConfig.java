package com.example.multitenancy_keycloak.configure;


import com.example.multitenancy_keycloak.tenant.TenantContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final TenantIssuerConfig issuerConfig;

    public SecurityConfig(TenantIssuerConfig issuerConfig) {
        this.issuerConfig = issuerConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.decoder(jwtDecoder()))
                );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            String tenant = TenantContext.getTenant();

            if (tenant == null) {
                throw new JwtException("Tenant not resolved");
            }

            String issuer = issuerConfig.getIssuer(tenant);

            if (issuer == null) {
                throw new JwtException("Unknown tenant");
            }

            return JwtDecoders
                    .fromIssuerLocation(issuer)
                    .decode(token);
        };
    }
}
