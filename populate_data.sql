-- CREATE SCHEMA IF NOT EXISTS crm_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE crm_db;

-- CLEAR registered client of OAuth2 server
DELETE FROM oauth2_authorization;
DELETE FROM oauth2_registered_client;
DELETE FROM jwk_storage;
-- CLEAR CRM app data (tables order matters for FK constraints)
DELETE FROM note;
DELETE FROM task;
DELETE FROM sales_order_item;
DELETE FROM sales_order;
DELETE FROM contact;
DELETE FROM product;
DELETE FROM user;
ALTER TABLE user AUTO_INCREMENT = 1;
ALTER TABLE contact AUTO_INCREMENT = 1;
ALTER TABLE sales_order AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;
ALTER TABLE note AUTO_INCREMENT = 1;
ALTER TABLE task AUTO_INCREMENT = 1;
ALTER TABLE sales_order_item AUTO_INCREMENT = 1;

-- ======================================
-- 1. USER TABLE (10 Users)
-- ======================================
INSERT INTO user (pk, name, first_name, last_name, username, password, email, phone, is_admin, is_staff, is_active, created_on, updated_on)
VALUES
(1, 'John Doe', 'John', 'Doe', 'jdoe', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'jdoe@example.com', '1234567890', 1, 1, 1, NOW(), NOW()),
(2, 'Jane Smith', 'Jane', 'Smith', 'jsmith', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'jsmith@example.com', '0987654321', 0, 0, 1, NOW(), NOW()),
(3, 'Michael Johnson', 'Michael', 'Johnson', 'mjohnson', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'mjohnson@example.com', '1112223333', 0, 0, 1, NOW(), NOW()),
(4, 'Emily Davis', 'Emily', 'Davis', 'edavis', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'edavis@example.com', '4445556666', 0, 0, 1, NOW(), NOW()),
(5, 'Robert Brown', 'Robert', 'Brown', 'rbrown', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'rbrown@example.com', '7778889999', 0, 0, 1, NOW(), NOW()),
(6, 'Olivia Wilson', 'Olivia', 'Wilson', 'owilson', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'owilson@example.com', '2223334444', 0, 0, 1, NOW(), NOW()),
(7, 'David Taylor', 'David', 'Taylor', 'dtaylor', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'dtaylor@example.com', '5556667777', 0, 0, 1, NOW(), NOW()),
(8, 'Sophia Miller', 'Sophia', 'Miller', 'smiller', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'smiller@example.com', '8889990000', 0, 0, 1, NOW(), NOW()),
(9, 'James Anderson', 'James', 'Anderson', 'janderson', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'janderson@example.com', '6667778888', 0, 0, 1, NOW(), NOW()),
(10, 'Linda Martinez', 'Linda', 'Martinez', 'lmartinez', '{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG', 'lmartinez@example.com', '3334445555', 0, 1, 0, NOW(), NOW());

-- ======================================
-- 2. CONTACT TABLE (30 contacts)
-- ======================================

SET @firstnames = 'John,Jane,Michael,Emily,Robert,Olivia,David,Sophia,James,Linda,William,Mary,Daniel,Emma,Joseph,Ava,Charles,Isabella,Thomas,Mia';
SET @lastnames  = 'Smith,Johnson,Williams,Brown,Jones,Miller,Davis,Garcia,Rodriguez,Martinez,Wilson,Anderson,Taylor,Thomas,Moore,Jackson,White,Harris,Martin,Thompson';
SET @orgs = 'TechNova,BlueOcean,InnovaSoft,GlobalCorp,NextGen Systems,Visionary Labs,DataEdge,AlphaWave,CloudSync,QuantumLogic,GreenByte,MetaPoint,CoreVision,FinTronix,UrbanTech,ClearLink,OmniData,NeoSphere,SysMatrix,PrimeWorks';

INSERT INTO contact (
  pk, contact_name, first_name, last_name, salutation, mobile_phone, email, organization,
  dob, lead_src, assigned_to, creator, address, description, created_on, updated_on
)
SELECT
  seq.n AS pk,
  CONCAT(
    SUBSTRING_INDEX(SUBSTRING_INDEX(@firstnames, ',', seq.fn_idx), ',', -1),
    ' ',
    SUBSTRING_INDEX(SUBSTRING_INDEX(@lastnames, ',', seq.ln_idx), ',', -1)
  ) AS contact_name,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@firstnames, ',', seq.fn_idx), ',', -1) AS first_name,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@lastnames, ',', seq.ln_idx), ',', -1) AS last_name,
  FLOOR(RAND() * 6) AS salutation,
  CONCAT('09', LPAD(FLOOR(RAND() * 99999999), 8, '0')) AS mobile_phone,
  CONCAT('contact', n, '@example.com') AS email,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@orgs, ',', FLOOR(1 + RAND() * 20)), ',', -1) AS organization,
  DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 15000) DAY) AS dob,
  FLOOR(RAND() * 6) AS lead_src,
  FLOOR(1 + RAND() * 10) AS assigned_to,
  FLOOR(1 + RAND() * 10) AS creator,
  CONCAT(FLOOR(100 + RAND() * 900), ' ',
         SUBSTRING_INDEX(SUBSTRING_INDEX(@lastnames, ',', seq.ln_idx), ',', -1),
         ' Street') AS address,
  CONCAT('Follow-up contact for ',
         SUBSTRING_INDEX(SUBSTRING_INDEX(@orgs, ',', FLOOR(1 + RAND() * 20)), ',', -1)) AS description,
  -- Spread created_on over the last 90 days so contacts are not all at NOW()
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY) AS created_on,
  -- updated_on within 0-30 days after created_on (but not in the future)
  LEAST(NOW(), DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 60) DAY)) AS updated_on
FROM (
WITH RECURSIVE seq AS (
  SELECT 1 AS n, FLOOR(1 + RAND() * 20) as fn_idx, FLOOR(1 + RAND() * 20) as ln_idx
  UNION ALL
  SELECT n + 1, FLOOR(1 + RAND() * 20) as fn_idx, FLOOR(1 + RAND() * 20) as ln_idx FROM seq WHERE n < 30
) SELECT * from seq
) AS seq;

-- ======================================
-- 3. PRODUCT TABLE (100 products)
-- ======================================

INSERT INTO product (
  pk, name, price, is_active, description, created_on, updated_on
)
SELECT
  seq3.n AS pk,
  CONCAT(
    ELT(FLOOR(RAND()*20)+1,
        'Ultra','Pro','Smart','Mega','Super','Eco','Compact','Prime','Advanced','Nano',
        'Hyper','NextGen','Elite','Fusion','Core','Max','Lite','Dynamic','Quantum','Turbo'
    ),
    ' ',
    ELT(FLOOR(RAND()*20)+1,
        'Phone','Tablet','Laptop','Headphones','Monitor','Camera','Router','Speaker','Mouse','Keyboard',
        'Watch','Drone','Printer','Projector','SSD','GPU','CPU','Microphone','Charger','Power Bank'
    )
  ) AS name,
  ROUND(50 + (RAND() * 1950), 3) AS price,
  IF(RAND() < 0.9, 1, 0) AS is_active,      -- 90% active
  CONCAT(
    'High quality ',
    ELT(FLOOR(RAND()*20)+1,
        'device','product','equipment','gadget','tool','accessory','component','solution','system','kit',
        'technology','hardware','item','instrument','gear','device','module','bundle','package','unit'
    ),
    ' for modern needs.'
  ) AS description,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 120) DAY) AS created_on,
  NOW() AS updated_on
FROM (
WITH RECURSIVE seq3 AS (
  SELECT 1 AS n
  UNION ALL
  SELECT n + 1 FROM seq3 WHERE n < 100
) SELECT * FROM seq3
) seq3;

-- ======================================
-- 4. SALES_ORDER TABLE (50 orders)
-- Spread created_on across last 7 days so aggregateDailyOrders has data
-- for yesterday and recent days when running daily report job.
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
  -- Spread over last 7 days: some today, some yesterday, etc.
  -- This ensures aggregateDailyOrders(startOfDay, endOfDay) always finds data
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 7) DAY) AS created_on,
  NOW() AS updated_on
FROM (
WITH RECURSIVE seq2 AS (
  SELECT 1 AS n
  UNION ALL
  SELECT n + 1 FROM seq2 WHERE n < 50
) SELECT * FROM seq2
) AS seq2;

-- ======================================
-- 5. SALES_ORDER_ITEM TABLE
-- Each sales order gets 1-3 line items linked to random products.
-- unit_price snapshots the product price; line_total is calculated.
-- ======================================

INSERT INTO sales_order_item (
  sales_order_fk, product_fk, quantity, unit_price, discount, line_total, sort_order, created_on, updated_on
)
SELECT
  so_pk AS sales_order_fk,
  prod_pk AS product_fk,
  qty AS quantity,
  p.price AS unit_price,
  disc AS discount,
  ROUND(p.price * qty * (1 - disc / 100), 2) AS line_total,
  item_idx AS sort_order,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 7) DAY) AS created_on,
  NOW() AS updated_on
FROM (
  -- Generate 1-3 items per sales order (total ~100 rows)
  WITH RECURSIVE so_items AS (
    SELECT 1 AS so_pk, 1 AS item_idx
    UNION ALL
    SELECT
      CASE WHEN item_idx >= FLOOR(1 + RAND() * 3) THEN so_pk + 1 ELSE so_pk END,
      CASE WHEN item_idx >= FLOOR(1 + RAND() * 3) THEN 1 ELSE item_idx + 1 END
    FROM so_items
    WHERE so_pk <= 50
  )
  SELECT
    so_pk,
    item_idx,
    FLOOR(1 + RAND() * 100) AS prod_pk,
    FLOOR(1 + RAND() * 5) AS qty,
    ROUND(RAND() * 15, 2) AS disc
  FROM so_items
  WHERE so_pk <= 50
  LIMIT 120
) AS items
JOIN product p ON p.pk = items.prod_pk;

-- ======================================
-- 6. NOTE TABLE (activity log entries)
-- Notes linked to contacts (pk 1-20) and sales orders (pk 1-15).
-- ======================================

SET @note_types = 'CALL,MEETING,EMAIL,NOTE';

-- Notes on contacts
INSERT INTO note (entity_type, entity_fk, note_type, content, created_by, created_on, updated_on)
SELECT
  'CONTACT' AS entity_type,
  FLOOR(1 + RAND() * 20) AS entity_fk,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@note_types, ',', FLOOR(1 + RAND() * 4)), ',', -1) AS note_type,
  ELT(FLOOR(RAND()*10)+1,
    'Discussed pricing and delivery timeline.',
    'Follow-up call regarding contract renewal.',
    'Sent product catalog via email.',
    'Meeting to review Q4 requirements.',
    'Left voicemail, will retry tomorrow.',
    'Client requested a demo session.',
    'Confirmed shipping address for upcoming order.',
    'Discussed feedback from previous purchase.',
    'Introduced new product line via email.',
    'Quick sync on outstanding invoices.'
  ) AS content,
  FLOOR(1 + RAND() * 10) AS created_by,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) AS created_on,
  NOW() AS updated_on
FROM (
WITH RECURSIVE seq4 AS (
  SELECT 1 AS n UNION ALL SELECT n + 1 FROM seq4 WHERE n < 20
) SELECT * FROM seq4
) AS seq4;

-- Notes on sales orders
INSERT INTO note (entity_type, entity_fk, note_type, content, created_by, created_on, updated_on)
SELECT
  'SALES_ORDER' AS entity_type,
  FLOOR(1 + RAND() * 15) AS entity_fk,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@note_types, ',', FLOOR(1 + RAND() * 4)), ',', -1) AS note_type,
  ELT(FLOOR(RAND()*8)+1,
    'Order approved by manager.',
    'Waiting for client payment confirmation.',
    'Delivery scheduled for next week.',
    'Partial shipment sent, remaining items pending.',
    'Client requested invoice revision.',
    'Price adjustment applied per agreement.',
    'Order put on hold pending credit check.',
    'Final review completed, ready for dispatch.'
  ) AS content,
  FLOOR(1 + RAND() * 10) AS created_by,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 14) DAY) AS created_on,
  NOW() AS updated_on
FROM (
WITH RECURSIVE seq5 AS (
  SELECT 1 AS n UNION ALL SELECT n + 1 FROM seq5 WHERE n < 15
) SELECT * FROM seq5
) AS seq5;

-- ======================================
-- 7. TASK TABLE
-- Mix of standalone tasks and tasks linked to contacts/sales orders.
-- Due dates spread from past (overdue) to future.
-- ======================================

SET @task_types = 'TODO,CALL,MEETING';

-- Tasks linked to contacts
INSERT INTO task (title, task_type, entity_type, entity_fk, status, priority, due_date, description, assigned_to, created_by, created_on, updated_on)
SELECT
  ELT(FLOOR(RAND()*8)+1,
    'Follow up with contact',
    'Schedule product demo',
    'Send updated proposal',
    'Review contract terms',
    'Prepare meeting agenda',
    'Collect feedback survey',
    'Update CRM records',
    'Arrange site visit'
  ) AS title,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@task_types, ',', FLOOR(1 + RAND() * 3)), ',', -1) AS task_type,
  'CONTACT' AS entity_type,
  FLOOR(1 + RAND() * 20) AS entity_fk,
  ELT(FLOOR(RAND()*3)+1, 'OPEN', 'IN_PROGRESS', 'DONE') AS status,
  ELT(FLOOR(RAND()*3)+1, 'LOW', 'MEDIUM', 'HIGH') AS priority,
  -- Due dates: some overdue (past), some upcoming (future)
  DATE_ADD(CURDATE(), INTERVAL FLOOR(-7 + RAND() * 21) DAY) AS due_date,
  CONCAT('Task related to contact #', FLOOR(1 + RAND() * 20)) AS description,
  FLOOR(1 + RAND() * 10) AS assigned_to,
  FLOOR(1 + RAND() * 10) AS created_by,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 14) DAY) AS created_on,
  NOW() AS updated_on
FROM (
WITH RECURSIVE seq6 AS (
  SELECT 1 AS n UNION ALL SELECT n + 1 FROM seq6 WHERE n < 12
) SELECT * FROM seq6
) AS seq6;

-- Tasks linked to sales orders
INSERT INTO task (title, task_type, entity_type, entity_fk, status, priority, due_date, description, assigned_to, created_by, created_on, updated_on)
SELECT
  ELT(FLOOR(RAND()*6)+1,
    'Process order payment',
    'Verify delivery address',
    'Coordinate with warehouse',
    'Generate invoice',
    'Schedule delivery',
    'Confirm order with client'
  ) AS title,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@task_types, ',', FLOOR(1 + RAND() * 3)), ',', -1) AS task_type,
  'SALES_ORDER' AS entity_type,
  FLOOR(1 + RAND() * 15) AS entity_fk,
  ELT(FLOOR(RAND()*3)+1, 'OPEN', 'IN_PROGRESS', 'DONE') AS status,
  ELT(FLOOR(RAND()*3)+1, 'LOW', 'MEDIUM', 'HIGH') AS priority,
  DATE_ADD(CURDATE(), INTERVAL FLOOR(-5 + RAND() * 15) DAY) AS due_date,
  CONCAT('Task related to sales order #', FLOOR(1 + RAND() * 15)) AS description,
  FLOOR(1 + RAND() * 10) AS assigned_to,
  FLOOR(1 + RAND() * 10) AS created_by,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10) DAY) AS created_on,
  NOW() AS updated_on
FROM (
WITH RECURSIVE seq7 AS (
  SELECT 1 AS n UNION ALL SELECT n + 1 FROM seq7 WHERE n < 8
) SELECT * FROM seq7
) AS seq7;

-- Standalone tasks (no entity link)
INSERT INTO task (title, task_type, entity_type, entity_fk, status, priority, due_date, description, assigned_to, created_by, created_on, updated_on)
SELECT
  ELT(FLOOR(RAND()*5)+1,
    'Weekly team sync',
    'Update sales pipeline report',
    'Review quarterly targets',
    'Clean up stale contacts',
    'Prepare monthly dashboard'
  ) AS title,
  SUBSTRING_INDEX(SUBSTRING_INDEX(@task_types, ',', FLOOR(1 + RAND() * 3)), ',', -1) AS task_type,
  NULL AS entity_type,
  NULL AS entity_fk,
  ELT(FLOOR(RAND()*3)+1, 'OPEN', 'IN_PROGRESS', 'DONE') AS status,
  ELT(FLOOR(RAND()*3)+1, 'LOW', 'MEDIUM', 'HIGH') AS priority,
  DATE_ADD(CURDATE(), INTERVAL FLOOR(-3 + RAND() * 14) DAY) AS due_date,
  'General CRM maintenance task' AS description,
  FLOOR(1 + RAND() * 10) AS assigned_to,
  FLOOR(1 + RAND() * 10) AS created_by,
  DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 7) DAY) AS created_on,
  NOW() AS updated_on
FROM (
WITH RECURSIVE seq8 AS (
  SELECT 1 AS n UNION ALL SELECT n + 1 FROM seq8 WHERE n < 5
) SELECT * FROM seq8
) AS seq8;
