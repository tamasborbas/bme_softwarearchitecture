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