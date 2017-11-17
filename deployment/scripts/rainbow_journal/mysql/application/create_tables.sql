CREATE SCHEMA IF NOT EXISTS `rainbow_journal` DEFAULT CHARACTER SET utf8 ;
USE `rainbow_journal`;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Table `file`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `file` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CREATION_DATE` DATETIME NOT NULL,
  `CREATOR` VARCHAR(255) NULL DEFAULT NULL,
  `FILE_CONTENT` LONGBLOB NOT NULL,
  `FILE_CONTENT_TYPE` VARCHAR(255) NOT NULL,
  `FILE_NAME` VARCHAR(255) NOT NULL,
  `FILE_SIZE` BIGINT(20) NOT NULL,
  `LAST_UPDATE_DATE` DATETIME NOT NULL,
  `UPDATER` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `profile`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `profile` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `BIRTH_DATE` DATE NULL DEFAULT NULL,
  `CREATION_DATE` DATETIME NOT NULL,
  `CREATOR` VARCHAR(255) NULL DEFAULT NULL,
  `DESCRIPTION` TEXT NULL DEFAULT NULL,
  `EMAIL` VARCHAR(255) NULL DEFAULT NULL,
  `FIRST_NAME` VARCHAR(255) NULL DEFAULT NULL,
  `LAST_NAME` VARCHAR(255) NOT NULL,
  `LAST_UPDATE_DATE` DATETIME NOT NULL,
  `UPDATER` VARCHAR(255) NULL DEFAULT NULL,
  `USER_NAME` VARCHAR(255) NOT NULL,
  `VERSION` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `USER_NAME` (`USER_NAME` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `journal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `journal` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `ACTIVE` TINYINT(1) NOT NULL DEFAULT '0',
  `CREATION_DATE` DATETIME NOT NULL,
  `CREATOR` VARCHAR(255) NULL DEFAULT NULL,
  `DESCRIPTION` TEXT NULL DEFAULT NULL,
  `LAST_UPDATE_DATE` DATETIME NOT NULL,
  `NAME` VARCHAR(255) NOT NULL,
  `TAG` VARCHAR(255) NULL DEFAULT NULL,
  `UPDATER` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` BIGINT(20) NULL DEFAULT NULL,
  `OWNER_PROFILE_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `NAME` (`NAME` ASC),
  INDEX `FK_JOURNAL_OWNER_PROFILE_ID` (`OWNER_PROFILE_ID` ASC),
  CONSTRAINT `FK_JOURNAL_OWNER_PROFILE_ID`
    FOREIGN KEY (`OWNER_PROFILE_ID`)
    REFERENCES `profile` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `journal_photo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `journal_photo` (
  `JOURNAL_ID` BIGINT(20) NOT NULL,
  `PHOTO_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`JOURNAL_ID`, `PHOTO_ID`),
  INDEX `FK_journal_photo_PHOTO_ID` (`PHOTO_ID` ASC),
  CONSTRAINT `FK_journal_photo_JOURNAL_ID`
    FOREIGN KEY (`JOURNAL_ID`)
    REFERENCES `journal` (`ID`),
  CONSTRAINT `FK_journal_photo_PHOTO_ID`
    FOREIGN KEY (`PHOTO_ID`)
    REFERENCES `file` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `profile_photo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `profile_photo` (
  `PROFILE_ID` BIGINT(20) NOT NULL,
  `PHOTO_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`PROFILE_ID`, `PHOTO_ID`),
  INDEX `FK_profile_photo_PHOTO_ID` (`PHOTO_ID` ASC),
  CONSTRAINT `FK_profile_photo_PHOTO_ID`
    FOREIGN KEY (`PHOTO_ID`)
    REFERENCES `file` (`ID`),
  CONSTRAINT `FK_profile_photo_PROFILE_ID`
    FOREIGN KEY (`PROFILE_ID`)
    REFERENCES `profile` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `publication`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `publication` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CREATION_DATE` DATETIME NOT NULL,
  `CREATOR` VARCHAR(255) NULL DEFAULT NULL,
  `DESCRIPTION` TEXT NULL DEFAULT NULL,
  `LAST_UPDATE_DATE` DATETIME NOT NULL,
  `PUBLICATION_DATE` DATETIME NULL DEFAULT NULL,
  `UPDATER` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` BIGINT(20) NULL DEFAULT NULL,
  `JOURNAL_ID` BIGINT(20) NOT NULL,
  `PUBLISHER_PROFILE_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_PUBLICATION_JOURNAL_ID` (`JOURNAL_ID` ASC),
  INDEX `FK_PUBLICATION_PUBLISHER_PROFILE_ID` (`PUBLISHER_PROFILE_ID` ASC),
  CONSTRAINT `FK_PUBLICATION_JOURNAL_ID`
    FOREIGN KEY (`JOURNAL_ID`)
    REFERENCES `journal` (`ID`),
  CONSTRAINT `FK_PUBLICATION_PUBLISHER_PROFILE_ID`
    FOREIGN KEY (`PUBLISHER_PROFILE_ID`)
    REFERENCES `profile` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `publication_file`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `publication_file` (
  `PUBLICATION_ID` BIGINT(20) NOT NULL,
  `FILE_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`PUBLICATION_ID`, `FILE_ID`),
  INDEX `FK_PUBLICATION_FILE_FILE_ID` (`FILE_ID` ASC),
  CONSTRAINT `FK_PUBLICATION_FILE_FILE_ID`
    FOREIGN KEY (`FILE_ID`)
    REFERENCES `file` (`ID`),
  CONSTRAINT `FK_PUBLICATION_FILE_PUBLICATION_ID`
    FOREIGN KEY (`PUBLICATION_ID`)
    REFERENCES `publication` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `subscription`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `subscription` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `CREATION_DATE` DATETIME NOT NULL,
  `CREATOR` VARCHAR(255) NULL DEFAULT NULL,
  `DESCRIPTION` TEXT NULL DEFAULT NULL,
  `LAST_UPDATE_DATE` DATETIME NOT NULL,
  `SUBSCRIPTION_DATE` DATETIME NULL DEFAULT NULL,
  `UPDATER` VARCHAR(255) NULL DEFAULT NULL,
  `VERSION` BIGINT(20) NULL DEFAULT NULL,
  `JOURNAL_ID` BIGINT(20) NOT NULL,
  `SUBSCRIBER_PROFILE_ID` BIGINT(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE INDEX `UNQ_SUBSCRIPTION_0` (`JOURNAL_ID` ASC, `SUBSCRIBER_PROFILE_ID` ASC),
  INDEX `FK_SUBSCRIPTION_SUBSCRIBER_PROFILE_ID` (`SUBSCRIBER_PROFILE_ID` ASC),
  CONSTRAINT `FK_SUBSCRIPTION_JOURNAL_ID`
    FOREIGN KEY (`JOURNAL_ID`)
    REFERENCES `journal` (`ID`),
  CONSTRAINT `FK_SUBSCRIPTION_SUBSCRIBER_PROFILE_ID`
    FOREIGN KEY (`SUBSCRIBER_PROFILE_ID`)
    REFERENCES `profile` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
