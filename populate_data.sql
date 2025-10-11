CREATE DATABASE IF NOT EXISTS crm_db;
USE crm_db;

-- CLEAR registered client of OAuth2 server
DELETE FROM oauth2_authorization;
DELETE FROM oauth2_registered_client;
DELETE FROM jwk_storage;
-- CLEAR CRM app data
DELETE FROM user;
DELETE FROM sales_order;
DELETE FROM contact;

-- ======================================
-- 1️. USER TABLE (10 Users)
-- ======================================
INSERT INTO user (pk, name, username, password, email, phone, is_admin, is_active, created_on, updated_on)
VALUES
(1, 'John Doe', 'jdoe', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'jdoe@example.com', '1234567890', 1, 1, NOW(), NOW()),
(2, 'Jane Smith', 'jsmith', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'jsmith@example.com', '0987654321', 0, 1, NOW(), NOW()),
(3, 'Michael Johnson', 'mjohnson', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'mjohnson@example.com', '1112223333', 0, 1, NOW(), NOW()),
(4, 'Emily Davis', 'edavis', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'edavis@example.com', '4445556666', 0, 1, NOW(), NOW()),
(5, 'Robert Brown', 'rbrown', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'rbrown@example.com', '7778889999', 0, 1, NOW(), NOW()),
(6, 'Olivia Wilson', 'owilson', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'owilson@example.com', '2223334444', 0, 1, NOW(), NOW()),
(7, 'David Taylor', 'dtaylor', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'dtaylor@example.com', '5556667777', 0, 1, NOW(), NOW()),
(8, 'Sophia Miller', 'smiller', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'smiller@example.com', '8889990000', 0, 1, NOW(), NOW()),
(9, 'James Anderson', 'janderson', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'janderson@example.com', '6667778888', 0, 1, NOW(), NOW()),
(10, 'Linda Martinez', 'lmartinez', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'lmartinez@example.com', '3334445555', 0, 1, NOW(), NOW());

-- ======================================
-- 2️. CONTACT TABLE (30 contacts)
-- ======================================

SET @firstnames = 'John,Jane,Michael,Emily,Robert,Olivia,David,Sophia,James,Linda,William,Mary,Daniel,Emma,Joseph,Ava,Charles,Isabella,Thomas,Mia';
SET @lastnames  = 'Smith,Johnson,Williams,Brown,Jones,Miller,Davis,Garcia,Rodriguez,Martinez,Wilson,Anderson,Taylor,Thomas,Moore,Jackson,White,Harris,Martin,Thompson';
SET @orgs = 'TechNova,BlueOcean,InnovaSoft,GlobalCorp,NextGen Systems,Visionary Labs,DataEdge,AlphaWave,CloudSync,QuantumLogic,GreenByte,MetaPoint,CoreVision,FinTronix,UrbanTech,ClearLink,OmniData,NeoSphere,SysMatrix,PrimeWorks';

INSERT INTO contact (
  contact_name, salutation, mobile_phone, email, organization,
  dob, lead_src, assigned_to, creator, address, description, created_on, updated_on
)
SELECT
  CONCAT(
    SUBSTRING_INDEX(SUBSTRING_INDEX(@firstnames, ',', FLOOR(1 + RAND() * 20)), ',', -1),
    ' ',
    SUBSTRING_INDEX(SUBSTRING_INDEX(@lastnames, ',', FLOOR(1 + RAND() * 20)), ',', -1)
  ) AS contact_name,
  FLOOR(RAND() * 6) AS salutation,
  CONCAT('09', LPAD(FLOOR(RAND() * 99999999), 8, '0')) AS mobile_phone,
  CONCAT('contact', n, '@example.com') AS email,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@orgs, ',', FLOOR(1 + RAND() * 20)), ',', -1) AS organization,
  DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 15000) DAY) AS dob,
  FLOOR(RAND() * 6) AS lead_src,
  FLOOR(1 + RAND() * 10) AS assigned_to,
  FLOOR(1 + RAND() * 10) AS creator,
  CONCAT(FLOOR(100 + RAND() * 900), ' ',
         SUBSTRING_INDEX(SUBSTRING_INDEX(@lastnames, ',', FLOOR(1 + RAND() * 20)), ',', -1),
         ' Street') AS address,
  CONCAT('Follow-up contact for ',
         SUBSTRING_INDEX(SUBSTRING_INDEX(@orgs, ',', FLOOR(1 + RAND() * 20)), ',', -1)) AS description,
  NOW(),
  NOW()
FROM (
WITH RECURSIVE seq AS (
  SELECT 1 AS n
  UNION ALL
  SELECT n + 1 FROM seq WHERE n < 30
) SELECT * from seq
) AS seq;

-- ======================================
-- 3️. SALES_ORDER TABLE (50 orders)
-- ======================================

SET @products = 'Laptop,Desktop,Monitor,Keyboard,Mouse,Printer,Router,Camera,Smartphone,Tablet,Software License,Cloud Service,Consulting Service,Training Package,Maintenance Contract';
SET @adjectives = 'Annual,Quarterly,Urgent,Standard,Express,Premium,Enterprise,Basic,Corporate,Custom';

INSERT INTO sales_order (
  subject, contact_fk, status, total, assigned_to, creator, description, created_on, updated_on
)
SELECT
  CONCAT(
    SUBSTRING_INDEX(SUBSTRING_INDEX(@adjectives, ',', FLOOR(1 + RAND() * 10)), ',', -1),
    ' ',
    SUBSTRING_INDEX(SUBSTRING_INDEX(@products, ',', FLOOR(1 + RAND() * 15)), ',', -1),
    ' - ',
    SUBSTRING_INDEX(SUBSTRING_INDEX(@orgs, ',', FLOOR(1 + RAND() * 20)), ',', -1)
  ) AS subject,
  FLOOR(1 + RAND() * 30) AS contact_fk,
  FLOOR(RAND() * 4) AS status,
  ROUND(RAND() * 20000, 2) AS total,
  FLOOR(1 + RAND() * 10) AS assigned_to,
  FLOOR(1 + RAND() * 10) AS creator,
  CONCAT('Sales order for ',
         SUBSTRING_INDEX(SUBSTRING_INDEX(@products, ',', FLOOR(1 + RAND() * 15)), ',', -1),
         ' placed by ',
         SUBSTRING_INDEX(SUBSTRING_INDEX(@orgs, ',', FLOOR(1 + RAND() * 20)), ',', -1)) AS description,
  NOW(),
  NOW()
FROM (
WITH RECURSIVE seq2 AS (
  SELECT 1 AS n
  UNION ALL
  SELECT n + 1 FROM seq2 WHERE n < 50
) SELECT * FROM seq2
) AS seq2;