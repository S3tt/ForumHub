-- Criação da base de dados
CREATE DATABASE IF NOT EXISTS `mydb`
CHARACTER SET utf8
COLLATE utf8_general_ci;

USE `mydb`;

-- -----------------------------------------------------
-- Tabela `perfil`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `perfil` (
  `idperfil` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idperfil`)
)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Tabela `curso`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `curso` (
  `idcurso` INT NOT NULL,
  `nome` VARCHAR(225) NOT NULL,
  `categoria` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`idcurso`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Tabela `usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `usuario` (
  `idusuario` INT NOT NULL,
  `nome` VARCHAR(225) NULL,
  `email` VARCHAR(225) NULL,
  `senha` VARCHAR(225) NULL,
  `perfil_id` INT NULL,
  `perfil_idperfil` INT NOT NULL,
  PRIMARY KEY (`idusuario`, `perfil_idperfil`),
  INDEX `fk_usuario_perfil1_idx` (`perfil_idperfil` ASC) VISIBLE,
  CONSTRAINT `fk_usuario_perfil1`
    FOREIGN KEY (`perfil_idperfil`)
    REFERENCES `perfil` (`idperfil`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Tabela `topico`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `topico` (
  `idtopico` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(45) NOT NULL,
  `data_criacao` DATETIME NOT NULL,
  `estado_topico` ENUM('ativo', 'inativo', 'arquivado') NOT NULL,
  `autor_id` INT NULL,
  `curso_id` INT NULL,
  `curso_idcurso` INT NOT NULL,
  `usuario_idusuario` INT NOT NULL,
  `resposta_idresposta` INT NOT NULL,
  PRIMARY KEY (`idtopico`, `curso_idcurso`, `usuario_idusuario`, `resposta_idresposta`),
  INDEX `fk_topico_curso_idx` (`curso_idcurso` ASC) VISIBLE,
  INDEX `fk_topico_usuario1_idx` (`usuario_idusuario` ASC) VISIBLE,
  INDEX `fk_topico_resposta1_idx` (`resposta_idresposta` ASC) VISIBLE,
  CONSTRAINT `fk_topico_curso`
    FOREIGN KEY (`curso_idcurso`)
    REFERENCES `curso` (`idcurso`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_topico_usuario1`
    FOREIGN KEY (`usuario_idusuario`)
    REFERENCES `usuario` (`idusuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_topico_resposta1`
    FOREIGN KEY (`resposta_idresposta`)
    REFERENCES `resposta` (`idresposta`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Tabela `resposta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `resposta` (
  `idresposta` INT NOT NULL AUTO_INCREMENT,
  `texto` TEXT NOT NULL,
  `data_resposta` DATETIME NOT NULL,
  `usuario_idusuario` INT NOT NULL,
  `topico_idtopico` INT NOT NULL,
  PRIMARY KEY (`idresposta`),
  INDEX `fk_resposta_usuario1_idx` (`usuario_idusuario` ASC) VISIBLE,
  INDEX `fk_resposta_topico1_idx` (`topico_idtopico` ASC) VISIBLE,
  CONSTRAINT `fk_resposta_usuario1`
    FOREIGN KEY (`usuario_idusuario`)
    REFERENCES `usuario` (`idusuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_resposta_topico1`
    FOREIGN KEY (`topico_idtopico`)
    REFERENCES `topico` (`idtopico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
ENGINE = InnoDB;
