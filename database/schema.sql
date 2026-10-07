-- Schéma de la base supermarket_db
-- Exécuter une seule fois : mysql -u root -p < database/schema.sql

CREATE DATABASE IF NOT EXISTS supermarket_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE supermarket_db;

CREATE TABLE IF NOT EXISTS ROLE (
    roleId   INT         NOT NULL AUTO_INCREMENT,
    roleName VARCHAR(50) NOT NULL,
    PRIMARY KEY (roleId)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS EMPLOYE (
    employeId    INT          NOT NULL AUTO_INCREMENT,
    firstName    VARCHAR(50)  NOT NULL,
    familyName   VARCHAR(50)  NOT NULL,
    birthDate    DATE         NOT NULL,
    address      VARCHAR(100) NULL,
    phoneNumber  VARCHAR(20)  NULL,
    passwordHash VARCHAR(255) NOT NULL,
    roleId       INT          NOT NULL,
    username     VARCHAR(50)  NOT NULL,
    PRIMARY KEY (employeId),
    UNIQUE KEY uk_employe_username (username),
    CONSTRAINT fk_employe_role FOREIGN KEY (roleId) REFERENCES ROLE (roleId)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS PRODUCT (
    productId   BIGINT       NOT NULL AUTO_INCREMENT,
    productName VARCHAR(100) NOT NULL,
    unitPrice   DOUBLE       NOT NULL,
    PRIMARY KEY (productId)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS STOCK (
    productId       BIGINT NOT NULL,
    currentQuantity INT    NOT NULL DEFAULT 0,
    minimalQuantity INT    NOT NULL DEFAULT 0,
    PRIMARY KEY (productId),
    CONSTRAINT fk_stock_product FOREIGN KEY (productId) REFERENCES PRODUCT (productId) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS SALE (
    saleId         INT           NOT NULL AUTO_INCREMENT,
    saleDate       DATETIME      NOT NULL,
    totalPrice     DECIMAL(10,2) NOT NULL,
    givenByClient  DECIMAL(10,2) NOT NULL,
    changeToReturn DECIMAL(10,2) NOT NULL,
    cashierId      INT           NOT NULL,
    PRIMARY KEY (saleId),
    CONSTRAINT fk_sale_cashier FOREIGN KEY (cashierId) REFERENCES EMPLOYE (employeId)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS SALE_DETAIL (
    ligneId       INT           NOT NULL AUTO_INCREMENT,
    quantitySold  INT           NOT NULL,
    unitSoldPrice DECIMAL(10,2) NOT NULL,
    productId     BIGINT        NOT NULL,
    saleId        INT           NOT NULL,
    PRIMARY KEY (ligneId),
    CONSTRAINT fk_detail_product FOREIGN KEY (productId) REFERENCES PRODUCT (productId),
    CONSTRAINT fk_detail_sale FOREIGN KEY (saleId) REFERENCES SALE (saleId) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS TICKET (
    ticketId INT NOT NULL AUTO_INCREMENT,
    saleId   INT NOT NULL,
    PRIMARY KEY (ticketId),
    CONSTRAINT fk_ticket_sale FOREIGN KEY (saleId) REFERENCES SALE (saleId) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Rôles attendus par l'application (DashboardView : 1 Manager, 2 Caissier, 3 Magasinier)
INSERT IGNORE INTO ROLE (roleId, roleName) VALUES
    (1, 'Manager'),
    (2, 'Caissier'),
    (3, 'Magasinier');
