CREATE DATABASE  IF NOT EXISTS crm_db;
USE crm_db;

DELETE FROM oauth2_authorization;
DELETE FROM oauth2_registered_client;
DELETE FROM jwk_storage;
DELETE FROM user;
DELETE FROM sales_order;
DELETE FROM contact;

INSERT INTO user VALUES
(1,'Name1','user1','{bcrypt}$2a$10$fO7/DMQiXzQiDEHLc944LOsC81FJrXEAUc2n1d.M4zWeuDMx7PtZG','user1@gmail.com','12346789',1,1,'2025-09-27 15:49:09','2025-09-27 15:49:09'),
(2,'Name2','user2','{bcrypt}$2a$10$UaqOO1So1q/DvfuG3zJtJuy2/CcspLCN4clfShUcvxt4vX1JVIOxu','user2@gmail.com','12346789',1,1,'2025-10-03 20:44:23','2025-10-03 20:44:23'),
(3,'Name3','user3','{bcrypt}$2a$10$xIMscQJJqAAgzUYfL.kXJuFwprXijD8OfKh6i8p9JSDn6h7.BRITe','user3@gmail.com','12346789',1,1,'2025-10-03 20:57:09','2025-10-03 20:57:09'),
(4,'Name4','User4','{bcrypt}$2a$10$xPB8GcIFVXREEMgTYOwB9uOx5RREHQR5X4CrKoeoBCZgFEJ76SKVO','user4@gmail.com','0124124124',1,0,'2025-10-04 08:34:21','2025-10-04 08:34:21'),
(5,'Name5','user5','{bcrypt}$2a$10$Ry.bOWJPaVr3kIcDnWf.O.v.1NLKQcWQEl1x8N3DDTa7MhXLIjiUi','user5@gmail.com','03124121221',0,0,'2025-10-04 08:42:08','2025-10-04 08:42:08');

INSERT INTO contact VALUES
(1,'Contactname1',4,'3123412341','cookcoo@gmail.com','CRM Org 1',NULL,2,1,1,'address1','des1','2025-10-04 01:48:24','2025-10-04 01:48:24'),
(2,'Contactname2',3,'0523523523','contact2@gmail.com','CRM Org 2',NULL,4,2,1,'address2','des2','2025-10-04 07:29:56','2025-10-04 07:29:56'),
(3,'Contactname3',5,'0421421414','contact3@gmail.com','CRM Org 3',NULL,0,4,1,'address3','des3','2025-10-04 21:39:25','2025-10-04 21:39:25');

INSERT INTO sales_order VALUES
(1,'Subject 1',2,2,421421421.00,2,1,'desciption 1','2025-10-04 08:04:45','2025-10-04 08:04:45'),
(2,'Subject 2',2,2,13144.00,2,1,'desciption 2','2025-10-04 08:42:54','2025-10-04 08:42:54'),
(3,'Subject 3',3,1,33333.00,3,1,'desciption 3','2025-10-04 21:39:52','2025-10-04 21:39:52');