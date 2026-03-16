-- USERS
INSERT INTO users VALUES
('ab6c0937-17ac-434e-80dd-c8b581d55935','Pedro Alves','pedro','pedro@email.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1998-05-10','ACTIVE',NOW(),NOW()),
('d101df10-ca1d-446a-a897-8ffed7950acb','Maria Costa','maria','maria@email.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1995-03-22','ACTIVE',NOW(),NOW()),
('966fc885-5107-4466-aa9a-ab7366e56fab','Lucas Lima','lucas','lucas@email.com','$2a$10$qCK6YDQuc9NW6cIuVLgtH.b9MrrNwWGM/HCQHtA33Qe1HhY8avKnS','1992-11-01','ACTIVE',NOW(),NOW());


-- CATEGORIES
INSERT INTO categories VALUES
('b0791d66-d480-402a-99fb-0a95c5f911ac','AUDIOVISUAL',NOW(),NOW()),
('9c826e49-9a0e-44cd-ac5f-8c12aea42766','EVENTS',NOW(),NOW()),
('701c8dc1-7e9b-46c9-85da-49e6d352190c','IT',NOW(),NOW()),
('040ffa1b-d075-4f52-8de1-0cfe90329958','CONSTRUCTION',NOW(),NOW());


-- SUBCATEGORIES
INSERT INTO sub_categories VALUES
('eba0769d-36fc-4d36-899f-65d12342d74e','Camera','b0791d66-d480-402a-99fb-0a95c5f911ac',NOW(),NOW()),
('47c5e3d5-5e5e-4bc2-a5b0-214f5835939b','Tripod','b0791d66-d480-402a-99fb-0a95c5f911ac',NOW(),NOW()),
('f7ead54f-d389-4246-a450-c93534520616','Sound System','9c826e49-9a0e-44cd-ac5f-8c12aea42766',NOW(),NOW()),
('0f956f80-aa28-4b6f-80a2-9dfc65609c2e','Laptop','701c8dc1-7e9b-46c9-85da-49e6d352190c',NOW(),NOW());


-- ADDRESSES
INSERT INTO addresses VALUES
('b64ea00e-53ca-4015-85db-c26629e3cda0','Rua das Flores','100',NULL,'Centro','Sorocaba','SP','Brazil','18130-000','ab6c0937-17ac-434e-80dd-c8b581d55935',NOW(),NOW()),
('a2f78f3c-b290-4573-b56a-311e4ecd2183','Av Brasil','250','Ap 12','Jardim','Sao Paulo','SP','Brazil','01000-000','d101df10-ca1d-446a-a897-8ffed7950acb', NOW(),NOW()),
('33bac100-e500-425e-aef0-270e664a959f','Rua Verde','45',NULL,'Centro','Campinas','SP','Brazil','13000-000','966fc885-5107-4466-aa9a-ab7366e56fab',NOW(),NOW());


-- ITEMS
INSERT INTO items VALUES
('9ec286a9-f11b-406a-87f5-956b612fb0cb','Canon T6 Camera','Canon','T6','DSLR camera for photography',80.00,'GOOD','eba0769d-36fc-4d36-899f-65d12342d74e','ab6c0937-17ac-434e-80dd-c8b581d55935','b64ea00e-53ca-4015-85db-c26629e3cda0',NOW(),NOW()),
('2e6fbb11-bbdf-426c-85d7-4ac0d52e81b2','Professional Tripod','Manfrotto','MK190XPRO4','Aluminum tripod',25.00,'EXCELLENT','47c5e3d5-5e5e-4bc2-a5b0-214f5835939b','ab6c0937-17ac-434e-80dd-c8b581d55935','b64ea00e-53ca-4015-85db-c26629e3cda0',NOW(),NOW()),
('efbeb34f-203b-4609-826f-ef49ffdb9c20','Dell XPS 15','Dell','9520','High performance laptop',120.00,'GOOD','0f956f80-aa28-4b6f-80a2-9dfc65609c2e','d101df10-ca1d-446a-a897-8ffed7950acb','a2f78f3c-b290-4573-b56a-311e4ecd2183',NOW(),NOW());

-- RENTALS
INSERT INTO rentals VALUES
('5f54c279-9ad6-40c2-8a32-22216d48ca39','ACTIVE','DAILY','2026-03-10 10:00:00','2026-03-12 10:00:00',160.00,'9ec286a9-f11b-406a-87f5-956b612fb0cb','d101df10-ca1d-446a-a897-8ffed7950acb',NOW(),NOW()),
('b24fca5b-648a-44e2-ba89-2223d8ab57b0','FINISHED','DAILY','2026-02-01 09:00:00','2026-02-03 09:00:00',240.00,'efbeb34f-203b-4609-826f-ef49ffdb9c20','ab6c0937-17ac-434e-80dd-c8b581d55935',NOW(),NOW());