-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema HR_app
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema HR_app
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `HR_app` ;
USE `HR_app` ;

-- -----------------------------------------------------
-- Table `HR_app`.`Teachers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HR_app`.`Teachers` (
  `teacher_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `last_name` VARCHAR(45) NOT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `availability` INT UNSIGNED NOT NULL,
  `timestamp` TIMESTAMP NOT NULL DEFAULT current_timestamp,
  PRIMARY KEY (`teacher_id`),
  INDEX `last_name` (`last_name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 1;


-- -----------------------------------------------------
-- Table `HR_app`.`Courses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `HR_app`.`Courses` (
  `c_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `semester` INT UNSIGNED NOT NULL,
  `hours` INT UNSIGNED NOT NULL,
  `teacher_id` INT UNSIGNED NULL,
  PRIMARY KEY (`c_id`),
  INDEX `teacher_id_idx` (`teacher_id` ASC) VISIBLE,
  CONSTRAINT `teacher_id`
    FOREIGN KEY (`teacher_id`)
    REFERENCES `HR_app`.`Teachers` (`teacher_id`)
    ON DELETE SET NULL
    ON UPDATE RESTRICT)
ENGINE = InnoDB
AUTO_INCREMENT = 1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;