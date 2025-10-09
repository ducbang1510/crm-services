package com.tdbang.crm.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Log4j2
@Configuration
public class JwtConfig {
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";
    private static final String ROLE_CLAIM_NAME = "roles";
    private static final String SCOPE_CLAIM_NAME = "scope";
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (context.getPrincipal() != null && context.getPrincipal().getAuthorities() != null) {
                log.info(context.getPrincipal().getAuthorities());
                var authorities = context.getPrincipal().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(auth -> auth.startsWith(ROLE_PREFIX))
                        .map(auth -> auth.substring(5))
                        .toList();

                context.getClaims().claim(ROLE_CLAIM_NAME, authorities);
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
        scopeConverter.setAuthorityPrefix(SCOPE_PREFIX);
        scopeConverter.setAuthoritiesClaimName(SCOPE_CLAIM_NAME);

        JwtGrantedAuthoritiesConverter roleConverter = new JwtGrantedAuthoritiesConverter();
        roleConverter.setAuthorityPrefix(ROLE_PREFIX);
        roleConverter.setAuthoritiesClaimName(ROLE_CLAIM_NAME);

        Converter<Jwt, Collection<GrantedAuthority>> combinedConverter = jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.addAll(scopeConverter.convert(jwt));
            authorities.addAll(roleConverter.convert(jwt));
            return authorities;
        };

        // Set combined converter into JwtAuthenticationConverter
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(combinedConverter);

        return jwtAuthenticationConverter;
    }
}
