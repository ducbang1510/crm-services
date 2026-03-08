CREATE TABLE IF NOT EXISTS email_job (
pk BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
entity_type VARCHAR(50) NOT NULL,
entity_pk BIGINT UNSIGNED NOT NULL,
recipient_email VARCHAR(200) NOT NULL,
recipient_name VARCHAR(255) NULL,
subject VARCHAR(255) NULL,
status VARCHAR(20) NOT NULL,
scheduled_at TIMESTAMP NOT NULL,
sent_at TIMESTAMP NULL,
error_message VARCHAR(500) NULL,
created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (pk),
KEY email_job_entity_ind (entity_type, entity_pk),
KEY email_job_status_ind (status)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

-- report_date is unique to prevent duplicate reports for the same day.
CREATE TABLE IF NOT EXISTS daily_sales_report (
pk BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
report_date DATE NOT NULL,
total_revenue DECIMAL(14,2) NOT NULL DEFAULT 0,
total_orders INT NOT NULL DEFAULT 0,
orders_created INT NOT NULL DEFAULT 0,
orders_approved INT NOT NULL DEFAULT 0,
orders_delivered INT NOT NULL DEFAULT 0,
orders_canceled INT NOT NULL DEFAULT 0,
mongo_file_id VARCHAR(200) NULL,
file_name VARCHAR(255) NULL,
status VARCHAR(20) NOT NULL,
error_message VARCHAR(500) NULL,
created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (pk),
UNIQUE KEY daily_sales_report_date_uq (report_date)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;
