-- -----------------------------------------------------
-- Table `global_configurations``
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `global_configurations` (
  `id` INT NOT NULL,
  `global_token_version` INT NOT NULL,
  `last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100),
  `birth_date` DATE,
  `user_status` VARCHAR(20) DEFAULT 'ACTIVE',
  `locale` VARCHAR(10) DEFAULT 'pt-BR',
  `timezone` VARCHAR(20) DEFAULT 'America/Sao_Paulo',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `token_version` INT NOT NULL DEFAULT 1,
  `global_version` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email`),
  UNIQUE INDEX `username_UNIQUE` (`username`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles` (
  `id` INT PRIMARY KEY,
  `name` VARCHAR(20)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` CHAR(36) NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  FOREIGN KEY (`role_id`)
    REFERENCES `roles` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `user_identities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_identities` (
	`id` CHAR(36) NOT NULL,
	`provider` VARCHAR(20) NOT NULL,
	`provider_user_id` VARCHAR(100) NOT NULL,
	`email` VARCHAR(100) NOT NULL,
	`email_verified` BOOLEAN DEFAULT FALSE,
	`avatar_url` VARCHAR(1024),
	`user_id` CHAR(36) NOT NULL,
	`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	`updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `uk_provider_user` (`provider`, `provider_user_id`),
	UNIQUE INDEX `uk_user_provider` (`user_id`, `provider`),
    INDEX `idx_user_id` (`user_id`),
	FOREIGN KEY (`user_id`)
     REFERENCES `users` (`id`)
     ON DELETE NO ACTION
     ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `categories` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(45) UNIQUE NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `sub_categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sub_categories` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(45) UNIQUE NOT NULL,
  `category_id` CHAR(36) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_sub_categories_category` (`category_id`),
  CONSTRAINT `fk_sub_categories_category`
    FOREIGN KEY (`category_id`)
    REFERENCES `categories` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `addresses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `addresses` (
  `id` CHAR(36) NOT NULL,
  `street` VARCHAR(120) NOT NULL,
  `number` VARCHAR(20) NOT NULL,
  `complement` VARCHAR(80) NULL,
  `district` VARCHAR(80) NOT NULL,
  `city` VARCHAR(80) NOT NULL,
  `state` VARCHAR(40) NOT NULL,
  `country` VARCHAR(40) NOT NULL,
  `zip_code` VARCHAR(20) NOT NULL,
  `user_id` CHAR(36) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_addresses_user` (`user_id`),
  INDEX `idx_addresses_city` (`city`),
  CONSTRAINT `fk_addresses_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `items`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `items` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `brand` VARCHAR(50) NOT NULL,
  `model` VARCHAR(50) NOT NULL,
  `description` LONGTEXT NULL,
  `base_price` DECIMAL(10,2) NOT NULL,
  `item_condition` VARCHAR(20) NOT NULL,
  `item_status` VARCHAR(20) NOT NULL,
  `sub_category_id` CHAR(36) NOT NULL,
  `owner_id` CHAR(36) NOT NULL,
  `pickup_address_id` CHAR(36) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_items_sub_category` (`sub_category_id`),
  INDEX `idx_items_owner` (`owner_id`),
  INDEX `idx_items_address` (`pickup_address_id`),
  CONSTRAINT `fk_items_sub_category`
    FOREIGN KEY (`sub_category_id`)
    REFERENCES `sub_categories` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_items_owner`
    FOREIGN KEY (`owner_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_items_address`
    FOREIGN KEY (`pickup_address_id`)
    REFERENCES `addresses` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `item_images`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `item_images` (
  `id` BINARY(16) NOT NULL,
  `storage_key` VARCHAR(200) NOT NULL,
  `display_order` INTEGER NOT NULL,
  `item_id` CHAR(36) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `item_images_item_order` (`item_id`, `display_order`),
  CONSTRAINT `fk_images_item`
    FOREIGN KEY (`item_id`)
    REFERENCES `items`(`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `rentals`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rentals` (
  `id` CHAR(36) NOT NULL,
  `rental_status` VARCHAR(20) NOT NULL,
  `rental_period` VARCHAR(20) NOT NULL,
  `start_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `final_price` DECIMAL(10,2) NOT NULL,
  `renter_id` CHAR(36) NOT NULL,
  `item_id` CHAR(36) NOT NULL,
  `owner_id` CHAR(36) NOT NULL,
  `expired_at` TIMESTAMP,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_rentals_renter` (`renter_id`),
  CONSTRAINT `fk_rentals_renter`
    FOREIGN KEY (`renter_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rentals_owner`
    FOREIGN KEY (`owner_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rentals_item`
    FOREIGN KEY (`item_id`)
    REFERENCES `items`(`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `payments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `payments`(
	`id` CHAR(36) NOT NULL,
	`payment_provider` VARCHAR(255),
	`external_payment_id` VARCHAR(255),
	`session_url` VARCHAR(512),
	`payment_intent` VARCHAR(255),
	`amount` DECIMAL(10,2) NOT NULL,
	`currency` VARCHAR(3) NOT NULL,
	`status` VARCHAR(20) NOT NULL,
	`rental_id` CHAR(36) NOT NULL,
	`paid_at` TIMESTAMP,
	`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	`updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   CONSTRAINT `fk_rentals_payment`
    FOREIGN KEY (`rental_id`)
    REFERENCES `rentals` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `address_snapshots`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `address_snapshots` (
  `id` CHAR(36) NOT NULL,
  `street` VARCHAR(120) NOT NULL,
  `number` VARCHAR(20) NOT NULL,
  `complement` VARCHAR(80) NULL,
  `district` VARCHAR(80) NOT NULL,
  `city` VARCHAR(80) NOT NULL,
  `state` VARCHAR(40) NOT NULL,
  `country` VARCHAR(40) NOT NULL,
  `zip_code` VARCHAR(20) NOT NULL,
  `rental_id` CHAR(36) NOT NULL,
  PRIMARY KEY (`rental_id`),
  INDEX `idx_addresses_snapshot_city` (`city`),
  CONSTRAINT `fk_addresses_snapshot_rental`
    FOREIGN KEY (`rental_id`)
    REFERENCES `rentals` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `item_snapshots``
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `item_snapshots` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `brand` VARCHAR(50) NOT NULL,
  `model` VARCHAR(50) NOT NULL,
  `description` LONGTEXT NULL,
  `base_price` DECIMAL(10,2) NOT NULL,
  `item_condition` VARCHAR(20) NOT NULL,
  `sub_category_name` VARCHAR(100),
  `rental_id` CHAR(36) NOT NULL,
  PRIMARY KEY (`rental_id`),
  CONSTRAINT `fk_item_snapshot_rental`
    FOREIGN KEY (`rental_id`)
    REFERENCES `rentals` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `audit_log``
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `audit_log` (
  `id` CHAR(36) NOT NULL,
  `action` VARCHAR(50) NOT NULL,
  `entity_id` CHAR(36) NOT NULL,
  `actor_id` CHAR(36),
  `current_body` JSON,
  `previous_body` JSON,
  `occurred_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_audit_entity` (`entity_id`),
  INDEX `idx_audit_actor` (`actor_id`),
  INDEX `idx_audit_action` (`action`),
  INDEX `idx_audit_occurred_at` (`occurred_at`)
) ENGINE=InnoDB;