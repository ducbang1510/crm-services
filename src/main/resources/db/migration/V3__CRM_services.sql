DELIMITER $$

DROP PROCEDURE IF EXISTS addColumnToTable $$
CREATE PROCEDURE addColumnToTable (tableName varchar(50), columnName varchar(50), columnType varchar(50),
                                   afterColumnName varchar(50), INOUT alterString varchar(1000))
BEGIN
    SET columnName = REPLACE(columnName, ' ','_');

    SET @alreadyExists = (SELECT 1 FROM information_schema.columns WHERE table_schema=database() AND table_name = tableName AND column_name = columnName limit 1);
    IF @alreadyExists is NULL
    THEN
        IF LENGTH(alterString) > 0
        THEN
            SET alterString = CONCAT(alterString, ', ');
END IF;

        SET alterString = CONCAT(alterString, " ADD column ", columnName ," ", columnType, " AFTER " , afterColumnName);
END IF;
END $$

DROP PROCEDURE IF EXISTS updateTable $$
CREATE PROCEDURE updateTable (tableName varchar(50), alterString varchar(1000))
BEGIN
    DECLARE CONTINUE HANDLER FOR 1060 BEGIN END;
    IF LENGTH(alterString) > 0
    THEN
        set @a = '';
        set @a = CONCAT("ALTER TABLE ", tableName , alterString);
PREPARE stmt FROM @a;
EXECUTE stmt;
END IF;
END $$
DELIMITER ;

-- Update User table
SET @alterString := '';
CALL addColumnToTable ( 'user', 'first_name', 'VARCHAR(255) NULL', 'name', @alterString);
CALL addColumnToTable ( 'user', 'last_name', 'VARCHAR(255) NULL', 'first_name', @alterString);
CALL updateTable ('user', @alterString);

-- Update Contact table
SET @alterString := '';
CALL addColumnToTable ( 'contact', 'first_name', 'VARCHAR(255) NULL', 'contact_name', @alterString);
CALL addColumnToTable ( 'contact', 'last_name', 'VARCHAR(255) NULL', 'first_name', @alterString);
CALL updateTable ('contact', @alterString);

DROP PROCEDURE IF EXISTS addColumnToTable;
DROP PROCEDURE IF EXISTS updateTable;

CREATE TABLE IF NOT EXISTS notification_message (
pk BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
sender_user_fk BIGINT UNSIGNED NOT NULL,
recipient_user_fk BIGINT UNSIGNED NOT NULL,
type INT,
message VARCHAR(255) NULL,
notification_object_fk BIGINT UNSIGNED NULL,
unread tinyint(1) DEFAULT 1,
created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;