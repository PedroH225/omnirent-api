-- -----------------------------------------------------
-- Table global_configurations
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS global_configurations (
  id INT NOT NULL,
  global_token_version INT NOT NULL,
  last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- -----------------------------------------------------
-- Table users
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(36) NOT NULL,
  name VARCHAR(100) NOT NULL,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(100),
  birth_date DATE,
  user_status VARCHAR(20) DEFAULT 'ACTIVE',
  locale VARCHAR(10) DEFAULT 'pt-BR',
  timezone VARCHAR(50) DEFAULT 'America/Sao_Paulo',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  token_version INT NOT NULL DEFAULT 1,
  global_version INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE (email),
  UNIQUE (username)
);

-- -----------------------------------------------------
-- Table roles
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS roles (
  id INT PRIMARY KEY,
  name VARCHAR(20)
);

-- -----------------------------------------------------
-- Table user_roles
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS user_roles (
  user_id VARCHAR(36) NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (role_id)
    REFERENCES roles (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table user_identities
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS user_identities (
  id VARCHAR(36) NOT NULL,
  provider VARCHAR(20) NOT NULL,
  provider_user_id VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  email_verified BOOLEAN DEFAULT FALSE,
  avatar_url VARCHAR(1024),
  user_id VARCHAR(36) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (provider, provider_user_id),
  UNIQUE (user_id, provider),
  FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_user_id
  ON user_identities (user_id);

-- -----------------------------------------------------
-- Table categories
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS categories (
  id VARCHAR(36) NOT NULL,
  name VARCHAR(45) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- -----------------------------------------------------
-- Table sub_categories
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS sub_categories (
  id VARCHAR(36) NOT NULL,
  name VARCHAR(45) UNIQUE NOT NULL,
  category_id VARCHAR(36) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (category_id)
    REFERENCES categories (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_sub_categories_category
  ON sub_categories (category_id);

-- -----------------------------------------------------
-- Table addresses
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS addresses (
  id VARCHAR(36) NOT NULL,
  street VARCHAR(120) NOT NULL,
  number VARCHAR(20) NOT NULL,
  complement VARCHAR(80),
  district VARCHAR(80) NOT NULL,
  city VARCHAR(80) NOT NULL,
  state VARCHAR(40) NOT NULL,
  country VARCHAR(40) NOT NULL,
  zip_code VARCHAR(20) NOT NULL,
  user_id VARCHAR(36) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_addresses_user
  ON addresses (user_id);

CREATE INDEX IF NOT EXISTS idx_addresses_city
  ON addresses (city);

-- -----------------------------------------------------
-- Table items
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS items (
  id VARCHAR(36) NOT NULL,
  name VARCHAR(100) NOT NULL,
  brand VARCHAR(50) NOT NULL,
  model VARCHAR(50) NOT NULL,
  description TEXT,
  base_price DECIMAL(10,2) NOT NULL,
  item_condition VARCHAR(20) NOT NULL,
  item_status VARCHAR(20) NOT NULL,
  sub_category_id VARCHAR(36) NOT NULL,
  owner_id VARCHAR(36) NOT NULL,
  pickup_address_id VARCHAR(36) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (sub_category_id)
    REFERENCES sub_categories (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (owner_id)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (pickup_address_id)
    REFERENCES addresses (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_items_sub_category
  ON items (sub_category_id);

CREATE INDEX IF NOT EXISTS idx_items_owner
  ON items (owner_id);

CREATE INDEX IF NOT EXISTS idx_items_address
  ON items (pickup_address_id);

-- -----------------------------------------------------
-- Table item_images
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS item_images (
  id UUID NOT NULL,
  storage_key VARCHAR(200) NOT NULL,
  display_order INTEGER NOT NULL,
  item_id VARCHAR(36) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (item_id, display_order),
  FOREIGN KEY (item_id)
    REFERENCES items (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table rentals
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS rentals (
  id VARCHAR(36) NOT NULL,
  rental_status VARCHAR(20) NOT NULL,
  rental_period VARCHAR(20) NOT NULL,
  start_date TIMESTAMP,
  end_date TIMESTAMP,
  final_price DECIMAL(10,2) NOT NULL,
  renter_id VARCHAR(36) NOT NULL,
  item_id VARCHAR(36) NOT NULL,
  owner_id VARCHAR(36) NOT NULL,
  expired_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (renter_id)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (owner_id)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (item_id)
    REFERENCES items (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_rentals_renter
  ON rentals (renter_id);

-- -----------------------------------------------------
-- Table payments
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS payments (
  id VARCHAR(36) NOT NULL,
  payment_provider VARCHAR(255),
  external_payment_id VARCHAR(255),
  session_url VARCHAR(512),
  payment_intent VARCHAR(255),
  amount DECIMAL(10,2) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  status VARCHAR(20) NOT NULL,
  rental_id VARCHAR(36) NOT NULL,
  paid_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (rental_id)
    REFERENCES rentals (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table address_snapshots
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS address_snapshots (
  id VARCHAR(36) NOT NULL,
  street VARCHAR(120) NOT NULL,
  number VARCHAR(20) NOT NULL,
  complement VARCHAR(80),
  district VARCHAR(80) NOT NULL,
  city VARCHAR(80) NOT NULL,
  state VARCHAR(40) NOT NULL,
  country VARCHAR(40) NOT NULL,
  zip_code VARCHAR(20) NOT NULL,
  rental_id VARCHAR(36) NOT NULL,
  PRIMARY KEY (rental_id),
  FOREIGN KEY (rental_id)
    REFERENCES rentals (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_addresses_snapshot_city
  ON address_snapshots (city);

-- -----------------------------------------------------
-- Table item_snapshots
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS item_snapshots (
  id VARCHAR(36) NOT NULL,
  name VARCHAR(100) NOT NULL,
  brand VARCHAR(50) NOT NULL,
  model VARCHAR(50) NOT NULL,
  description TEXT,
  base_price DECIMAL(10,2) NOT NULL,
  item_condition VARCHAR(20) NOT NULL,
  sub_category_name VARCHAR(100),
  thumbnail_key VARCHAR(200),
  rental_id VARCHAR(36) NOT NULL,
  PRIMARY KEY (rental_id),
  FOREIGN KEY (rental_id)
    REFERENCES rentals (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table audit_log
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS audit_log (
  id VARCHAR(36) NOT NULL,
  action VARCHAR(50) NOT NULL,
  entity_id VARCHAR(36) NOT NULL,
  actor_id VARCHAR(36),
  current_body TEXT,
  previous_body TEXT,
  occurred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_audit_entity
  ON audit_log (entity_id);

CREATE INDEX IF NOT EXISTS idx_audit_actor
  ON audit_log (actor_id);

CREATE INDEX IF NOT EXISTS idx_audit_action
  ON audit_log (action);

CREATE INDEX IF NOT EXISTS idx_audit_occurred_at
  ON audit_log (occurred_at);