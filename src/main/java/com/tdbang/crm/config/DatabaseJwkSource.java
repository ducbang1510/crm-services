package com.tdbang.crm.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import com.tdbang.crm.entities.JwkEntity;
import com.tdbang.crm.repositories.JpaJwkRepository;

@Component
public class DatabaseJwkSource {

    private final JpaJwkRepository jpaJwkRepository;

    public DatabaseJwkSource(JpaJwkRepository jpaJwkRepository) {
        this.jpaJwkRepository = jpaJwkRepository;
    }

    @PostConstruct
    public void initialize() {
        // Ensure we have at least one key
        if (jpaJwkRepository.count() == 0) {
            generateAndSaveNewKey();
        }
    }

    public JWKSource<SecurityContext> getJwkSource(String kid) {
        JwkEntity jwkEntity = jpaJwkRepository.findById(kid)
                .orElseGet(this::generateAndSaveNewKey);

        try {
            RSAKey rsaKey = RSAKey.parse(jwkEntity.getJwkJson());
            JWKSet jwkSet = new JWKSet(rsaKey);

            // Update last used timestamp
            jwkEntity.setLastUsed(Instant.now());
            jpaJwkRepository.save(jwkEntity);

            return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load JWK from database", e);
        }
    }

    private JwkEntity generateAndSaveNewKey() {
        try {
            RSAKey rsaKey = generateRsaKey();
            String jwkJson = rsaKey.toJSONString();

            JwkEntity entity = new JwkEntity();
            entity.setKid("default-key");
            entity.setJwkJson(jwkJson);
            entity.setCreatedAt(Instant.now());
            entity.setLastUsed(Instant.now());

            return jpaJwkRepository.save(entity);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate new JWK", e);
        }
    }

    private RSAKey generateRsaKey() {
        KeyPair keyPair = generateRsaKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("default-key")
                .build();
    }

    private KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
