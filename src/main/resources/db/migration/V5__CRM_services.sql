CREATE TABLE IF NOT EXISTS file_attachment (
pk BIGINT UNSIGNED AUTO_INCREMENT,
entity_type VARCHAR(100),        -- e.g. 'CONTACT', 'SALES_ORDER'
entity_fk BIGINT UNSIGNED,       -- fk to contact.pk or sales_order.pk
file_name VARCHAR(255),
content_type VARCHAR(100),
size BIGINT,
mongo_file_id VARCHAR(255),
uploaded_by BIGINT UNSIGNED,
uploaded_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
description VARCHAR(512),
is_active TINYINT(1) DEFAULT 1,
PRIMARY KEY (pk),
KEY idx_entity (entity_type, entity_fk)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;
