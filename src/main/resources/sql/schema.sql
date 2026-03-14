-- MySQL Workbench Forward Engineering

DROP SCHEMA IF EXISTS `omnirent_db`;

CREATE SCHEMA IF NOT EXISTS `omnirent_db`;
USE `omnirent_db`;

-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `omnirent_db`.`users` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `birth_date` DATE NOT NULL,
  `user_status` VARCHAR(20) DEFAULT 'ACTIVE',
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email`),
  UNIQUE INDEX `username_UNIQUE` (`username`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `categories` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(45) UNIQUE NOT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `sub_categories`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sub_categories` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(45) UNIQUE NOT NULL,
  `category_id` CHAR(36) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
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
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
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
  `sub_category_id` CHAR(36) NOT NULL,
  `owner_id` CHAR(36) NOT NULL,
  `pickup_address_id` CHAR(36) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
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
-- Table `rentals`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rentals` (
  `id` CHAR(36) NOT NULL,
  `rental_status` VARCHAR(20) NOT NULL,
  `rental_period` VARCHAR(20) NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `final_price` DECIMAL(10,2) NOT NULL,
  `item_id` CHAR(36) NOT NULL,
  `renter_id` CHAR(36) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_rentals_item` (`item_id`),
  INDEX `idx_rentals_renter` (`renter_id`),
  CONSTRAINT `fk_rentals_item`
    FOREIGN KEY (`item_id`)
    REFERENCES `items` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rentals_renter`
    FOREIGN KEY (`renter_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB;