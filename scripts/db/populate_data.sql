-- Increase timeout and memory if needed (optional, depends on MySQL config)
SET SESSION innodb_lock_wait_timeout = 300;

DELIMITER //

DROP PROCEDURE IF EXISTS PopulateData //

CREATE PROCEDURE PopulateData()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE batch_size INT DEFAULT 1000;
    DECLARE total_records INT DEFAULT 1000000;
    DECLARE current_address_id INT;
    
    WHILE i <= total_records DO
        START TRANSACTION;
        
        SET @j = 0;
        WHILE @j < batch_size AND i <= total_records DO
            -- Insert address
            INSERT INTO address (street, city, state, zip_code)
            VALUES (
                CONCAT(FLOOR(100 + RAND() * 8999), ' ', ELT(FLOOR(1 + RAND() * 4), 'Main St', 'Oak Ave', 'Cedar Ln', 'Pine Rd')),
                ELT(FLOOR(1 + RAND() * 5), 'New York', 'Los Angeles', 'Chicago', 'Houston', 'Phoenix'),
                ELT(FLOOR(1 + RAND() * 5), 'NY', 'CA', 'IL', 'TX', 'AZ'),
                CONCAT(FLOOR(10000 + RAND() * 89999))
            );
            
            SET current_address_id = LAST_INSERT_ID();
            
            -- Insert person with first_name, last_name, age, and address_id
            INSERT INTO person (first_name, last_name, age, address_id)
            VALUES (
                CONCAT('User', i),
                CONCAT('Last', i),
                FLOOR(18 + RAND() * 62), -- Random age between 18 and 80
                current_address_id
            );
            
            SET i = i + 1;
            SET @j = @j + 1;
        END WHILE;
        
        COMMIT;
    END WHILE;
END //

DELIMITER ;

-- Execute the population
SELECT 'Starting data population... this may take several minutes.' AS status;
CALL PopulateData();
SELECT 'Data population completed.' AS status;

-- Cleanup the procedure
DROP PROCEDURE IF EXISTS PopulateData;
