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
  `idcurso` INT NOT NULL auto_increment,
  `nome` VARCHAR(225) NOT NULL,
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
CREATE TABLE topico (
    idtopico INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL, -- Mudando para 'conteudo' para armazenar a descrição/pergunta do tópico
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_topico VARCHAR(20) NOT NULL DEFAULT 'ativo',
    autor_id INT NOT NULL,
    curso_idcurso INT NOT NULL,
    resposta_idresposta INT NULL,
    FOREIGN KEY (autor_id) REFERENCES usuario(idusuario),
    FOREIGN KEY (curso_idcurso) REFERENCES curso(idcurso),
    FOREIGN KEY (resposta_idresposta) REFERENCES resposta(idresposta) ON DELETE SET NULL
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;



	CREATE TABLE resposta (
		idresposta INT AUTO_INCREMENT PRIMARY KEY,
		conteudo_resposta TEXT NOT NULL,
		data_resposta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
		usuario_idusuario INT NOT NULL,
		topico_idtopico INT NOT NULL,
		FOREIGN KEY (usuario_idusuario) REFERENCES usuario(idusuario),
		FOREIGN KEY (topico_idtopico) REFERENCES topico(idtopico)
	)
	ENGINE = InnoDB
	DEFAULT CHARACTER SET = utf8;

select * from usuario;
ALTER TABLE topico DROP FOREIGN KEY fk_topico_usuario1;
ALTER TABLE usuario MODIFY COLUMN idusuario INT AUTO_INCREMENT;
ALTER TABLE topico
ADD CONSTRAINT fk_topico_usuario1 FOREIGN KEY (usuario_idusuario) REFERENCES usuario(idusuario);
SELECT * FROM perfil WHERE idperfil = 1;
INSERT INTO curso (idcurso, nome) VALUES (1, 'Curso de portugues');
DESCRIBE topico;
alter table curso;
DROP TABLE IF EXISTS topico;
DROP TABLE IF EXISTS resposta;
ALTER TABLE topico ADD CONSTRAINT topico_ibfk_3 FOREIGN KEY (resposta_idresposta) REFERENCES resposta(idresposta);


DESCRIBE resposta;

ALTER TABLE topico ADD COLUMN tipo_conteudo VARCHAR(255);

ALTER TABLE topico
ADD COLUMN estado_topico VARCHAR(50) DEFAULT 'ativo';



ALTER TABLE curso DROP COLUMN categoria;
ALTER TABLE topico ADD COLUMN conteudo TEXT;
ALTER TABLE topico
ADD COLUMN pergunta TEXT;





SELECT * FROM usuario;

SELECT * FROM curso WHERE idcurso = 1;

INSERT INTO perfil (idperfil, nome) VALUES (1, 'Admin');

INSERT INTO curso (nome) VALUES ('Matemática');
INSERT INTO curso (nome) VALUES ('Ciências');
INSERT INTO curso (nome) VALUES ('Português');
INSERT INTO topico (titulo, autor_id, curso_idcurso) 
VALUES ('Como aprender álgebra?', 2, 1);

SELECT * FROM usuario;

ALTER TABLE topico DROP COLUMN estado_topico;
ALTER TABLE topico ADD COLUMN autor_nome VARCHAR(255);
UPDATE topico t
JOIN usuario u ON t.autor_id = u.idusuario
SET t.autor_nome = u.nome;

-- Remover a chave estrangeira existente
ALTER TABLE resposta DROP FOREIGN KEY resposta_ibfk_2;

-- Adicionar a chave estrangeira com a cláusula ON DELETE CASCADE
ALTER TABLE resposta 
ADD CONSTRAINT resposta_ibfk_2 
FOREIGN KEY (topico_idtopico) 
REFERENCES topico(idtopico) 
ON DELETE CASCADE;

