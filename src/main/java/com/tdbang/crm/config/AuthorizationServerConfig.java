package com.tdbang.crm.config;

import java.time.Duration;
import java.util.UUID;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import com.tdbang.crm.authentication.DatabaseJwkSource;

@Configuration
public class AuthorizationServerConfig implements InitializingBean {
    @Value("${authorization-server.jwk-source.kid}")
    private String kidId;
    @Value("${authorization-server.client.id}")
    private String clientId;
    @Value("${authorization-server.client.secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.authorization-server.redirect-uri}")
    private String redirectURI;

    @Value("${spring.security.oauth2.authorization-server.token.authorization-code-time-to-live:10}")
    private int authCodeTimeToLive;
    @Value("${spring.security.oauth2.authorization-server.token.access-token-time-to-live:60}")
    private int accessTokenTimeToLive;
    @Value("${spring.security.oauth2.authorization-server.token.refresh-token-time-to-live:10080}")
    private int refreshTokenTimeToLive;

    @Value("${spring.security.oauth2.authorization-server.swagger-redirect-uri}")
    private String swaggerRedirectURI;
    @Value("${authorization-server.swagger-client.id}")
    private String swaggerClientId;
    @Value("${authorization-server.swagger-client.secret}")
    private String swaggerClientSecret;

    private final DatabaseJwkSource databaseJwkSource;

    public AuthorizationServerConfig(DatabaseJwkSource databaseJwkSource) {
        this.databaseJwkSource = databaseJwkSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        JdbcRegisteredClientRepository jdbcRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        initializeDefaultClients(jdbcRepository);
        return jdbcRepository;
    }

    @Bean
    public OAuth2AuthorizationService oauth2AuthorizationService(JdbcTemplate jdbcTemplate,
                                                                 RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
                                                                         RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    private void initializeDefaultClients(RegisteredClientRepository clientRepository) {
        if (clientRepository.findByClientId(clientId) == null) {
            RegisteredClient crmAppClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri(redirectURI)
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope("api:read")
                    .scope("api:write")
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(false)
                            .requireAuthorizationConsent(false)
                            .build())
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofMinutes(accessTokenTimeToLive))
                            .refreshTokenTimeToLive(Duration.ofMinutes(refreshTokenTimeToLive))
                            .authorizationCodeTimeToLive(Duration.ofMinutes(authCodeTimeToLive))
                            .reuseRefreshTokens(false)
                            .build())
                    .build();

            clientRepository.save(crmAppClient);
        }

        if (clientRepository.findByClientId(swaggerClientId) == null) {
            RegisteredClient swaggerClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(swaggerClientId)
                    .clientSecret(swaggerClientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri("http://127.0.0.1:8080/swagger-ui/oauth2-redirect.html")
                    .redirectUri("http://localhost:8080/swagger-ui/oauth2-redirect.html")
                    .scope("api:read")
                    .scope("api:write")
                    .clientSettings(ClientSettings.builder()
                            .requireProofKey(false)
                            .requireAuthorizationConsent(false)
                            .build())
                    .build();
            clientRepository.save(swaggerClient);
        }
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8080")
                .authorizationEndpoint("/oauth2/authorize")
                .tokenEndpoint("/oauth2/token")
                .tokenIntrospectionEndpoint("/oauth2/introspect")
                .tokenRevocationEndpoint("/oauth2/revoke")
                .jwkSetEndpoint("/oauth2/jwks")
                .oidcUserInfoEndpoint("/userinfo")
                .oidcClientRegistrationEndpoint("/connect/register")
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        return databaseJwkSource.getJwkSource(kidId);
    }
}
