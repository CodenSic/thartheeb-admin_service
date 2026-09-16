package com.thartheeb.admin.config;

import com.thartheeb.admin.common.SecurityErrorHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityErrorHandler errors) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/internal/**").hasAuthority("SCOPE_internal")
                .requestMatchers("/v1/admin/**").hasAnyRole("THARTHEEB_ADMIN", "COMPLIANCE_REVIEWER")
                .anyRequest().authenticated())
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(errors)
                .accessDeniedHandler(errors))
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt ->
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
            .build();
    }

    private Converter<Jwt, ? extends org.springframework.security.authentication.AbstractAuthenticationToken>
    jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            String scope = jwt.getClaimAsString("scope");
            if (scope != null) {
                for (String value : scope.split(" ")) {
                    if (!value.isBlank()) authorities.add(new SimpleGrantedAuthority("SCOPE_" + value));
                }
            }
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) roles.forEach(role ->
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
            return authorities;
        });
        return converter;
    }
}
