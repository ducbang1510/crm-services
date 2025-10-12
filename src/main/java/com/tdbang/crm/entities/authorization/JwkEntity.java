package com.tdbang.crm.entities.authorization;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "jwk_storage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwkEntity {
    @Id
    private String kid = "default-key";

    @Lob
    @Column(name = "jwk_json", length = 4000)
    private String jwkJson;

    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "last_used")
    private Instant lastUsed;
}
