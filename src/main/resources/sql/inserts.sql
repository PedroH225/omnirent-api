-- =========================
-- GLOBAL_CONFIGURATIONS
-- =========================
INSERT IGNORE INTO global_configurations (id, global_token_version) VALUES
(1, 1);

-- =========================
-- ROLES
-- =========================
INSERT IGNORE INTO roles(id, name) VALUES
(1, "ROLE_USER"),
(2, "ROLE_ADMIN");

-- =========================
-- USERS
-- =========================
INSERT IGNORE INTO users (id, name, username, email, password, birth_date, user_status) VALUES
('ab6c0937-17ac-434e-80dd-c8b581d55935','Pedro Alves','pedro','pedrodguimaraes@hotmail.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1998-05-10','ACTIVE'),
('d101df10-ca1d-446a-a897-8ffed7950acb','Maria Costa','maria','maria@example.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1995-03-22','ACTIVE'),
('966fc885-5107-4466-aa9a-ab7366e56fab','Lucas Lima','lucas','lucas@example.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1992-11-01','ACTIVE'),
('9a26d72f-9b7d-4b97-a842-5d1455d90571','João Pereira','joao','joao@example.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1994-08-12','ACTIVE'),
('0d34b3ab-5b85-4ddf-8d2d-4e9bcd3a9d63','Ana Souza','ana','ana@example.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1997-01-20','ACTIVE'),
('e75f8c6d-8a6e-49b2-ae59-1f4b9b6e31cb','Carlos Mendes','carlos','carlos@example.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1990-12-01','ACTIVE'),
('4d83260d-92fb-45cb-b5e5-92f7d9dbfc53','Fernanda Rocha','fernanda','fernanda@example.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1996-07-03','ACTIVE');

-- =========================
-- USER_ROLES
-- =========================
INSERT IGNORE INTO user_roles(user_id, role_id) VALUES
('ab6c0937-17ac-434e-80dd-c8b581d55935',1),
('d101df10-ca1d-446a-a897-8ffed7950acb',1),
('966fc885-5107-4466-aa9a-ab7366e56fab',1),
('9a26d72f-9b7d-4b97-a842-5d1455d90571',1),
('0d34b3ab-5b85-4ddf-8d2d-4e9bcd3a9d63',1),
('e75f8c6d-8a6e-49b2-ae59-1f4b9b6e31cb',1),
('4d83260d-92fb-45cb-b5e5-92f7d9dbfc53',1);

-- =========================
-- CATEGORIES
-- =========================
INSERT IGNORE INTO categories(id, name) VALUES
('b0791d66-d480-402a-99fb-0a95c5f911ac','AUDIOVISUAL'),
('9c826e49-9a0e-44cd-ac5f-8c12aea42766','EVENTS'),
('701c8dc1-7e9b-46c9-85da-49e6d352190c','IT'),
('040ffa1b-d075-4f52-8de1-0cfe90329958','CONSTRUCTION');


-- =========================
-- SUBCATEGORIES
-- =========================
INSERT IGNORE INTO sub_categories(id, name, category_id) VALUES
-- AUDIOVISUAL
('eba0769d-36fc-4d36-899f-65d12342d74e','CAMERA','b0791d66-d480-402a-99fb-0a95c5f911ac'),
('47c5e3d5-5e5e-4bc2-a5b0-214f5835939b','TRIPOD','b0791d66-d480-402a-99fb-0a95c5f911ac'),
('8d5d5077-86f2-4c95-bf0d-44cf5f5ecf18','DRONE','b0791d66-d480-402a-99fb-0a95c5f911ac'),
('a13bfa44-c3b8-4df1-a8dd-8a0bcbf3f991','LENS','b0791d66-d480-402a-99fb-0a95c5f911ac'),
('2cc0c7f1-0f31-4db6-bc91-3eab2dbdce31','MICROPHONE','b0791d66-d480-402a-99fb-0a95c5f911ac'),
('5db1f33d-d7cf-46c4-8b9e-f0b3bdb761cb','LIGHTING','b0791d66-d480-402a-99fb-0a95c5f911ac'),

-- EVENTS
('f7ead54f-d389-4246-a450-c93534520616','SOUNDSYSTEM','9c826e49-9a0e-44cd-ac5f-8c12aea42766'),
('9dd91db8-dfb6-4706-8a60-5ff9c3ec6e59','PROJECTOR','9c826e49-9a0e-44cd-ac5f-8c12aea42766'),
('d4c7a7aa-6c7a-4d52-8fd8-8b5c98a3a4dc','STAGE','9c826e49-9a0e-44cd-ac5f-8c12aea42766'),
('f5e8d3f1-53d7-4e88-9d6e-0b4f20e8db22','TENT','9c826e49-9a0e-44cd-ac5f-8c12aea42766'),
('bc2c2e49-56a5-4cb4-9e54-29fd7d2df9db','CHAIR','9c826e49-9a0e-44cd-ac5f-8c12aea42766'),

-- IT
('0f956f80-aa28-4b6f-80a2-9dfc65609c2e','LAPTOP','701c8dc1-7e9b-46c9-85da-49e6d352190c'),
('7b0dc0d2-4650-4d78-bff9-5c23a9f8cf51','MONITOR','701c8dc1-7e9b-46c9-85da-49e6d352190c'),
('51c77954-7f92-4a5d-a8eb-07f0efcbf01c','SERVER','701c8dc1-7e9b-46c9-85da-49e6d352190c'),
('cd4a0f3d-3a56-4e3d-a6a9-7a9d9f2a89c1','TABLET','701c8dc1-7e9b-46c9-85da-49e6d352190c'),
('b5c0f1a2-7f4c-4c6d-8e21-6f9b0a6dcb87','NETWORKING','701c8dc1-7e9b-46c9-85da-49e6d352190c'),

-- CONSTRUCTION
('ec93511d-daa4-4c55-a6b6-c6a65ebd14fb','DRILL','040ffa1b-d075-4f52-8de1-0cfe90329958'),
('77d3a93e-1cbb-4d5d-8bb6-7b27ef8d99c3','CEMENT_MIXER','040ffa1b-d075-4f52-8de1-0cfe90329958'),
('f8d8a1a2-3e65-4cb4-86b1-9f4e77c7c1c8','GENERATOR','040ffa1b-d075-4f52-8de1-0cfe90329958'),
('1d3d0f7a-8f41-4fcb-8c54-0c7a6c7b4c92','LADDER','040ffa1b-d075-4f52-8de1-0cfe90329958'),
('8b3e8c9a-5f64-4b75-a4f7-8d3e6a0a4f22','PRESSURE_WASHER','040ffa1b-d075-4f52-8de1-0cfe90329958');


-- =========================
-- ADDRESS
-- =========================
INSERT IGNORE INTO addresses(id, street, number, complement, district, city, state, country, zip_code, user_id) VALUES
('b64ea00e-53ca-4015-85db-c26629e3cda0','Rua das Flores','100',NULL,'Centro','Sorocaba','SP','Brazil','18130-000','ab6c0937-17ac-434e-80dd-c8b581d55935'),
('a2f78f3c-b290-4573-b56a-311e4ecd2183','Av Brasil','250','Ap 12','Jardim','Sao Paulo','SP','Brazil','01000-000','d101df10-ca1d-446a-a897-8ffed7950acb'),
('33bac100-e500-425e-aef0-270e664a959f','Rua Verde','45',NULL,'Centro','Campinas','SP','Brazil','13000-000','966fc885-5107-4466-aa9a-ab7366e56fab'),
('abcf307a-057f-413b-86b8-6b643f0d8dd6','Rua Vermelha','50',NULL,'Centro','Campinas','SP','Brazil','13000-000','966fc885-5107-4466-aa9a-ab7366e56fab'),
('f3efb2a7-cd11-44f5-a8d3-d0c57e7d0d41','Rua Alpha','15',NULL,'Centro','Sorocaba','SP','Brazil','18000-000','9a26d72f-9b7d-4b97-a842-5d1455d90571'),
('29d95a17-4d1c-4db6-a6fb-4cb8dc03d9cf','Rua Beta','120',NULL,'Jardim Europa','Sorocaba','SP','Brazil','18010-000','0d34b3ab-5b85-4ddf-8d2d-4e9bcd3a9d63'),
('5d783c8e-f5d4-4b84-a842-615c8f2f8b43','Rua Gamma','88',NULL,'Centro','Campinas','SP','Brazil','13020-000','e75f8c6d-8a6e-49b2-ae59-1f4b9b6e31cb'),
('dc64d9e0-25ef-4d86-b5a2-baeec93c6729','Rua Delta','510',NULL,'Vila Nova','São Paulo','SP','Brazil','02020-000','4d83260d-92fb-45cb-b5e5-92f7d9dbfc53');

-- =========================
-- ITEMS
-- =========================
INSERT IGNORE INTO items(id, name, brand, model, description, base_price, item_condition, item_status, sub_category_id, owner_id, pickup_address_id, created_at, updated_at) VALUES
('9ec286a9-f11b-406a-87f5-956b612fb0cb','Canon T6 Camera','Canon','T6','DSLR camera for photography',80.00,'GOOD','AVAILABLE','eba0769d-36fc-4d36-899f-65d12342d74e','ab6c0937-17ac-434e-80dd-c8b581d55935','b64ea00e-53ca-4015-85db-c26629e3cda0','2026-07-10 14:30:00', NOW()),
('2e6fbb11-bbdf-426c-85d7-4ac0d52e81b2','Manfrotto Professional Tripod','Manfrotto','MK190XPRO4','Professional aluminum tripod',25.00,'LIKE_NEW','AVAILABLE','47c5e3d5-5e5e-4bc2-a5b0-214f5835939b','ab6c0937-17ac-434e-80dd-c8b581d55935','b64ea00e-53ca-4015-85db-c26629e3cda0','2026-06-20 09:15:00', NOW());

INSERT IGNORE INTO items(id, name, brand, model, description, base_price, item_condition, item_status, sub_category_id, owner_id, pickup_address_id) VALUES
('efbeb34f-203b-4609-826f-ef49ffdb9c20','Dell XPS 15','Dell','9520','High performance laptop for professional use',120.00,'GOOD','AVAILABLE','0f956f80-aa28-4b6f-80a2-9dfc65609c2e','d101df10-ca1d-446a-a897-8ffed7950acb','a2f78f3c-b290-4573-b56a-311e4ecd2183'),
('b9a99f4d-3b38-4f9c-9d2a-7864dddb1a01','Sony A6400 Camera','Sony','A6400','Mirrorless camera for professional photography',95.00,'LIKE_NEW','AVAILABLE','eba0769d-36fc-4d36-899f-65d12342d74e','9a26d72f-9b7d-4b97-a842-5d1455d90571','f3efb2a7-cd11-44f5-a8d3-d0c57e7d0d41'),
('cb93d2f6-6d85-48a4-a73c-73452b91af02','DJI Mini 3 Pro Drone','DJI','Mini 3 Pro','Drone for aerial photography and video',180.00,'LIKE_NEW','AVAILABLE','8d5d5077-86f2-4c95-bf0d-44cf5f5ecf18','9a26d72f-9b7d-4b97-a842-5d1455d90571','f3efb2a7-cd11-44f5-a8d3-d0c57e7d0d41'),
('2efda34b-9d79-4f34-a01d-f8ebc63866b4','Epson Full HD Projector','Epson','PowerLite','Projector for presentations and events',70.00,'GOOD','AVAILABLE','9dd91db8-dfb6-4706-8a60-5ff9c3ec6e59','0d34b3ab-5b85-4ddf-8d2d-4e9bcd3a9d63','29d95a17-4d1c-4db6-a6fb-4cb8dc03d9cf'),
('d77c26d4-96cf-4b0c-a95f-3a6fb8ec14d3','Godox LED Light Kit','Godox','SL60W','Continuous lighting kit for video production',60.00,'GOOD','AVAILABLE','5db1f33d-d7cf-46c4-8b9e-f0b3bdb761cb','0d34b3ab-5b85-4ddf-8d2d-4e9bcd3a9d63','29d95a17-4d1c-4db6-a6fb-4cb8dc03d9cf'),
('05f1b89e-7292-4ea3-935d-f66729bdbb31','MacBook Pro 16','Apple','M3 Pro','High performance laptop for development and design',180.00,'LIKE_NEW','AVAILABLE','0f956f80-aa28-4b6f-80a2-9dfc65609c2e','e75f8c6d-8a6e-49b2-ae59-1f4b9b6e31cb','5d783c8e-f5d4-4b84-a842-615c8f2f8b43'),
('05f1b89e-7292-4ea3-935d-f66729bdbb32','LG UltraWide Monitor','LG','29WN600','Ultrawide monitor for productivity',45.00,'GOOD','AVAILABLE','7b0dc0d2-4650-4d78-bff9-5c23a9f8cf51','e75f8c6d-8a6e-49b2-ae59-1f4b9b6e31cb','5d783c8e-f5d4-4b84-a842-615c8f2f8b43'),
('d8f2d9fd-cf4b-4f4c-9c8c-8f3dba55b8de','Dell PowerEdge Server','Dell','T340','Server equipment for temporary infrastructure',220.00,'USED','AVAILABLE','51c77954-7f92-4a5d-a8eb-07f0efcbf01c','e75f8c6d-8a6e-49b2-ae59-1f4b9b6e31cb','5d783c8e-f5d4-4b84-a842-615c8f2f8b43'),
('68cfad79-bfc0-49c5-85a0-5fcbca4d0d22','Bosch Rotary Hammer','Bosch','GBH 2-26','Rotary hammer drill for construction work',40.00,'GOOD','AVAILABLE','ec93511d-daa4-4c55-a6b6-c6a65ebd14fb','4d83260d-92fb-45cb-b5e5-92f7d9dbfc53','dc64d9e0-25ef-4d86-b5a2-baeec93c6729'),
('68cfad79-bfc0-49c5-85a0-5fcbca4d0d23','Honda Portable Generator','Honda','EU22i','Portable generator for construction and events',90.00,'LIKE_NEW','AVAILABLE','f8d8a1a2-3e65-4cb4-86b1-9f4e77c7c1c8','4d83260d-92fb-45cb-b5e5-92f7d9dbfc53','dc64d9e0-25ef-4d86-b5a2-baeec93c6729'),
('68cfad79-bfc0-49c5-85a0-5fcbca4d0d24','Aluminum Extension Ladder','Worker','7 Steps','Aluminum ladder for maintenance work',35.00,'GOOD','AVAILABLE','1d3d0f7a-8f41-4fcb-8c54-0c7a6c7b4c92','4d83260d-92fb-45cb-b5e5-92f7d9dbfc53','dc64d9e0-25ef-4d86-b5a2-baeec93c6729'),
('c45c3c43-640b-4dc7-a23d-7c0e82dd731f','JBL PartyBox 710','JBL','PartyBox 710','High-power speaker for parties and events',95.00,'LIKE_NEW','AVAILABLE','f7ead54f-d389-4246-a450-c93534520616','4d83260d-92fb-45cb-b5e5-92f7d9dbfc53','dc64d9e0-25ef-4d86-b5a2-baeec93c6729'),
('d9854d89-b81d-4b73-a9ec-87e28f5fd0ce','Canon RF 70-200mm Lens','Canon','RF 70-200mm f/2.8','Professional telephoto lens',120.00,'LIKE_NEW','AVAILABLE','a13bfa44-c3b8-4df1-a8dd-8a0bcbf3f991','9a26d72f-9b7d-4b97-a842-5d1455d90571','f3efb2a7-cd11-44f5-a8d3-d0c57e7d0d41'),
('a6afaf26-19a2-4d91-a9a0-80cf5f598fb7','Portable PA Sound System','Yamaha','StagePas 400BT','Portable sound system for live events',110.00,'GOOD','AVAILABLE','f7ead54f-d389-4246-a450-c93534520616','e75f8c6d-8a6e-49b2-ae59-1f4b9b6e31cb','5d783c8e-f5d4-4b84-a842-615c8f2f8b43');

-- =========================
-- RENTALS
-- =========================
INSERT IGNORE INTO rentals (id, rental_status, rental_period, final_price, renter_id, owner_id, item_id) VALUES
('5f54c279-9ad6-40c2-8a32-22216d48ca39','CREATED','DAILY',160.00,'d101df10-ca1d-446a-a897-8ffed7950acb','ab6c0937-17ac-434e-80dd-c8b581d55935', '2e6fbb11-bbdf-426c-85d7-4ac0d52e81b2'),
('b24fca5b-648a-44e2-ba89-2223d8ab57b0','CONFIRMED','DAILY',240.00,'ab6c0937-17ac-434e-80dd-c8b581d55935','d101df10-ca1d-446a-a897-8ffed7950acb', 'efbeb34f-203b-4609-826f-ef49ffdb9c20');

-- =========================
-- ITEM SNAPSHOTS
-- =========================
INSERT IGNORE INTO item_snapshots VALUES
('3db9a3e6-0368-45ac-9200-8ee3d0a10f44','Canon T6 Camera','Canon','T6', 'DSLR camera for photography', 80.00,'GOOD','Camera', '5f54c279-9ad6-40c2-8a32-22216d48ca39'),
('faefe02d-cc3e-4e33-aefb-2b3e556fabc2','Dell XPS 15','Dell','9520', 'High performance laptop',120.00,'GOOD','Laptop', 'b24fca5b-648a-44e2-ba89-2223d8ab57b0');

-- =========================
-- ADDRESS_SNAPSHOTS
-- =========================
INSERT IGNORE INTO address_snapshots VALUES
('80b6696f-d7fe-4f0d-81b9-3ae47ab7de99','Rua das Flores','123',NULL,'Centro','São Paulo','SP','Brasil','01001-000', '5f54c279-9ad6-40c2-8a32-22216d48ca39'),
('3f9ce4e3-8fec-4782-b9d5-c6ddf3607f8b','Av. Paulista','1578','Apto 42','Bela Vista','São Paulo','SP','Brasil','01310-200', 'b24fca5b-648a-44e2-ba89-2223d8ab57b0');