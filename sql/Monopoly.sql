-- MySQL Script generated by MySQL Workbench
-- 11/27/14 21:47:01
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema monopoly
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema monopoly
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `monopoly` DEFAULT CHARACTER SET utf8 ;
USE `monopoly` ;

-- -----------------------------------------------------
-- Table `monopoly`.`building`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`building` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `BASENIGHTPAYMENT` INT(11) NULL DEFAULT NULL,
  `HOUSEPRICE` INT(11) NULL DEFAULT NULL,
  `NAME` VARCHAR(255) NULL DEFAULT NULL,
  `PERHOUSEPAYMENT` INT(11) NULL DEFAULT NULL,
  `PRICE` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`user` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `EMAIL` VARCHAR(255) NULL DEFAULT NULL,
  `NAME` VARCHAR(255) NULL DEFAULT NULL,
  `PASSWORDHASH` VARCHAR(255) NULL DEFAULT NULL,
  `USERTYPE` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`player`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`player` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `MONEY` INT(11) NULL DEFAULT NULL,
  `PLAYERSTATUS` VARCHAR(255) NULL DEFAULT NULL,
  `GAME_ID` INT(11) NULL DEFAULT NULL,
  `USER_ID` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_PLAYER_GAME_ID` (`GAME_ID` ASC),
  INDEX `FK_PLAYER_USER_ID` (`USER_ID` ASC),
  CONSTRAINT `FK_PLAYER_GAME_ID`
    FOREIGN KEY (`GAME_ID`)
    REFERENCES `monopoly`.`game` (`ID`),
  CONSTRAINT `FK_PLAYER_USER_ID`
    FOREIGN KEY (`USER_ID`)
    REFERENCES `monopoly`.`user` (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 31
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`game`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`game` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `GAMESTATUS` VARCHAR(255) NULL DEFAULT NULL,
  `NAME` VARCHAR(255) NULL DEFAULT NULL,
  `ACTUALPLAYER_ID` INT(11) NULL DEFAULT NULL,
  `OWNEROFGAME_ID` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_GAME_ACTUALPLAYER_ID` (`ACTUALPLAYER_ID` ASC),
  INDEX `FK_GAME_OWNEROFGAME_ID` (`OWNEROFGAME_ID` ASC),
  CONSTRAINT `FK_GAME_ACTUALPLAYER_ID`
    FOREIGN KEY (`ACTUALPLAYER_ID`)
    REFERENCES `monopoly`.`player` (`ID`),
  CONSTRAINT `FK_GAME_OWNEROFGAME_ID`
    FOREIGN KEY (`OWNEROFGAME_ID`)
    REFERENCES `monopoly`.`user` (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`place`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`place` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `DTYPE` VARCHAR(31) NULL DEFAULT NULL,
  `PLACESEQUENCENUMBER` INT(11) NULL DEFAULT NULL,
  `GAME_ID` INT(11) NULL DEFAULT NULL,
  `HOUSENUMBER` INT(11) NULL DEFAULT NULL,
  `BUILDING_ID` INT(11) NULL DEFAULT NULL,
  `OWNERPLAYER_ID` INT(11) NULL DEFAULT NULL,
  `THROUGHMONEY` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_PLACE_GAME_ID` (`GAME_ID` ASC),
  INDEX `FK_PLACE_BUILDING_ID` (`BUILDING_ID` ASC),
  INDEX `FK_PLACE_OWNERPLAYER_ID` (`OWNERPLAYER_ID` ASC),
  CONSTRAINT `FK_PLACE_BUILDING_ID`
    FOREIGN KEY (`BUILDING_ID`)
    REFERENCES `monopoly`.`building` (`ID`),
  CONSTRAINT `FK_PLACE_GAME_ID`
    FOREIGN KEY (`GAME_ID`)
    REFERENCES `monopoly`.`game` (`ID`),
  CONSTRAINT `FK_PLACE_OWNERPLAYER_ID`
    FOREIGN KEY (`OWNERPLAYER_ID`)
    REFERENCES `monopoly`.`player` (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 161
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`game_place`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`game_place` (
  `Game_ID` INT(11) NOT NULL,
  `places_ID` INT(11) NOT NULL,
  PRIMARY KEY (`Game_ID`, `places_ID`),
  INDEX `FK_GAME_PLACE_places_ID` (`places_ID` ASC),
  CONSTRAINT `FK_GAME_PLACE_Game_ID`
    FOREIGN KEY (`Game_ID`)
    REFERENCES `monopoly`.`game` (`ID`),
  CONSTRAINT `FK_GAME_PLACE_places_ID`
    FOREIGN KEY (`places_ID`)
    REFERENCES `monopoly`.`place` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`game_player`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`game_player` (
  `Game_ID` INT(11) NOT NULL,
  `players_ID` INT(11) NOT NULL,
  PRIMARY KEY (`Game_ID`, `players_ID`),
  INDEX `FK_GAME_PLAYER_players_ID` (`players_ID` ASC),
  CONSTRAINT `FK_GAME_PLAYER_Game_ID`
    FOREIGN KEY (`Game_ID`)
    REFERENCES `monopoly`.`game` (`ID`),
  CONSTRAINT `FK_GAME_PLAYER_players_ID`
    FOREIGN KEY (`players_ID`)
    REFERENCES `monopoly`.`player` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`housebuying`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`housebuying` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `BUYEDHOUSENUMBER` INT(11) NULL DEFAULT NULL,
  `FORBUILDING_ID` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_HOUSEBUYING_FORBUILDING_ID` (`FORBUILDING_ID` ASC),
  CONSTRAINT `FK_HOUSEBUYING_FORBUILDING_ID`
    FOREIGN KEY (`FORBUILDING_ID`)
    REFERENCES `monopoly`.`place` (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`player_place`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`player_place` (
  `Player_ID` INT(11) NOT NULL,
  `buildings_ID` INT(11) NOT NULL,
  PRIMARY KEY (`Player_ID`, `buildings_ID`),
  INDEX `FK_PLAYER_PLACE_buildings_ID` (`buildings_ID` ASC),
  CONSTRAINT `FK_PLAYER_PLACE_Player_ID`
    FOREIGN KEY (`Player_ID`)
    REFERENCES `monopoly`.`player` (`ID`),
  CONSTRAINT `FK_PLAYER_PLACE_buildings_ID`
    FOREIGN KEY (`buildings_ID`)
    REFERENCES `monopoly`.`place` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`step`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`step` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `BUYEDBUILDING_ID` INT(11) NULL DEFAULT NULL,
  `FINISHPLACE_ID` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_STEP_FINISHPLACE_ID` (`FINISHPLACE_ID` ASC),
  INDEX `FK_STEP_BUYEDBUILDING_ID` (`BUYEDBUILDING_ID` ASC),
  CONSTRAINT `FK_STEP_BUYEDBUILDING_ID`
    FOREIGN KEY (`BUYEDBUILDING_ID`)
    REFERENCES `monopoly`.`place` (`ID`),
  CONSTRAINT `FK_STEP_FINISHPLACE_ID`
    FOREIGN KEY (`FINISHPLACE_ID`)
    REFERENCES `monopoly`.`place` (`ID`))
ENGINE = InnoDB
AUTO_INCREMENT = 69
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`player_step`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`player_step` (
  `Player_ID` INT(11) NOT NULL,
  `steps_ID` INT(11) NOT NULL,
  PRIMARY KEY (`Player_ID`, `steps_ID`),
  INDEX `FK_PLAYER_STEP_steps_ID` (`steps_ID` ASC),
  CONSTRAINT `FK_PLAYER_STEP_Player_ID`
    FOREIGN KEY (`Player_ID`)
    REFERENCES `monopoly`.`player` (`ID`),
  CONSTRAINT `FK_PLAYER_STEP_steps_ID`
    FOREIGN KEY (`steps_ID`)
    REFERENCES `monopoly`.`step` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`step_housebuying`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`step_housebuying` (
  `Step_ID` INT(11) NOT NULL,
  `houseBuyings_ID` INT(11) NOT NULL,
  PRIMARY KEY (`Step_ID`, `houseBuyings_ID`),
  INDEX `FK_STEP_HOUSEBUYING_houseBuyings_ID` (`houseBuyings_ID` ASC),
  CONSTRAINT `FK_STEP_HOUSEBUYING_Step_ID`
    FOREIGN KEY (`Step_ID`)
    REFERENCES `monopoly`.`step` (`ID`),
  CONSTRAINT `FK_STEP_HOUSEBUYING_houseBuyings_ID`
    FOREIGN KEY (`houseBuyings_ID`)
    REFERENCES `monopoly`.`housebuying` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`step_place`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`step_place` (
  `Step_ID` INT(11) NOT NULL,
  `soldBuildings_ID` INT(11) NOT NULL,
  PRIMARY KEY (`Step_ID`, `soldBuildings_ID`),
  INDEX `FK_STEP_PLACE_soldBuildings_ID` (`soldBuildings_ID` ASC),
  CONSTRAINT `FK_STEP_PLACE_Step_ID`
    FOREIGN KEY (`Step_ID`)
    REFERENCES `monopoly`.`step` (`ID`),
  CONSTRAINT `FK_STEP_PLACE_soldBuildings_ID`
    FOREIGN KEY (`soldBuildings_ID`)
    REFERENCES `monopoly`.`place` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `monopoly`.`user_player`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `monopoly`.`user_player` (
  `User_ID` INT(11) NOT NULL,
  `gamePlayers_ID` INT(11) NOT NULL,
  PRIMARY KEY (`User_ID`, `gamePlayers_ID`),
  INDEX `FK_USER_PLAYER_gamePlayers_ID` (`gamePlayers_ID` ASC),
  CONSTRAINT `FK_USER_PLAYER_User_ID`
    FOREIGN KEY (`User_ID`)
    REFERENCES `monopoly`.`user` (`ID`),
  CONSTRAINT `FK_USER_PLAYER_gamePlayers_ID`
    FOREIGN KEY (`gamePlayers_ID`)
    REFERENCES `monopoly`.`player` (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


-- -----------------------------------------------------
-- Insert default data to `monopoly`.`building`
-- -----------------------------------------------------
INSERT INTO `monopoly`.`building` 
	(`BASENIGHTPAYMENT`, `HOUSEPRICE`, `NAME`, `PERHOUSEPAYMENT`, `PRICE`) 
	VALUES ('170', '100', 'NKE', '110', '600');
INSERT INTO `monopoly`.`building` 
	(`BASENIGHTPAYMENT`, `HOUSEPRICE`, `NAME`, `PERHOUSEPAYMENT`, `PRICE`) 
	VALUES ('150', '50', 'OE', '60', '500');
INSERT INTO `monopoly`.`building` 
	(`BASENIGHTPAYMENT`, `HOUSEPRICE`, `NAME`, `PERHOUSEPAYMENT`, `PRICE`) 
	VALUES ('400', '300', 'BCE', '350', '1500');
INSERT INTO `monopoly`.`building` 
	(`BASENIGHTPAYMENT`, `HOUSEPRICE`, `NAME`, `PERHOUSEPAYMENT`, `PRICE`) 
	VALUES ('400', '300', 'Zene Akadémia', '370', '1400');
INSERT INTO `monopoly`.`building` 
	(`BASENIGHTPAYMENT`, `HOUSEPRICE`, `NAME`, `PERHOUSEPAYMENT`, `PRICE`) 
	VALUES ('200', '100', 'ELTE', '150', '800');
INSERT INTO `monopoly`.`building` 
	(`BASENIGHTPAYMENT`, `HOUSEPRICE`, `NAME`, `PERHOUSEPAYMENT`, `PRICE`) 
	VALUES ('150', '50', 'BGF', '60', '450');
INSERT INTO `monopoly`.`building` 
	(`BASENIGHTPAYMENT`, `HOUSEPRICE`, `NAME`, `PERHOUSEPAYMENT`, `PRICE`) 
	VALUES ('400', '200', 'SOTE', '300', '1200');
INSERT INTO `monopoly`.`building` 
	(`BASENIGHTPAYMENT`, `HOUSEPRICE`, `NAME`, `PERHOUSEPAYMENT`, `PRICE`) 
	VALUES ('300', '200', 'BME', '350', '1000');