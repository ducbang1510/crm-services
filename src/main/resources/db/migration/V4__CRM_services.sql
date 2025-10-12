CREATE TABLE IF NOT EXISTS product (
pk BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
name VARCHAR(255) NOT NULL,
price DECIMAL(14,3) NOT NULL,
is_active tinyint(1) NOT NULL DEFAULT '0',
description VARCHAR(255) NULL,
created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (pk)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

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
CALL addColumnToTable ( 'user', 'is_staff', 'TINYINT(1) NOT NULL DEFAULT 0', 'is_admin', @alterString);
CALL updateTable ('user', @alterString);

DROP PROCEDURE IF EXISTS addColumnToTable;
DROP PROCEDURE IF EXISTS updateTable;