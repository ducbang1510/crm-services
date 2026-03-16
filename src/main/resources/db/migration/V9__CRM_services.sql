/*
 * Copyright © 2026 by tdbang.
 * All rights reserved.
 */

-- AI Agent audit log table
CREATE TABLE ai_audit_log (
    pk BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL,
    user_pk BIGINT UNSIGNED NULL,
    action VARCHAR(50) NOT NULL,
    request_body TEXT NULL,
    response_body TEXT NULL,
    model_used VARCHAR(100) NULL,
    tokens_in INT NULL,
    tokens_out INT NULL,
    latency_ms INT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (pk),
    KEY idx_ai_audit_session (session_id),
    KEY idx_ai_audit_user (user_pk),
    KEY idx_ai_audit_created (created_on)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;
