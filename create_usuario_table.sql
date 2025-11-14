-- Script SQL para criar a tabela de usuários
-- Execute este script no seu banco de dados

-- Criar tabela USUARIO
CREATE TABLE USUARIO (
    IDUSUARIO INT PRIMARY KEY AUTO_INCREMENT,
    NOME VARCHAR(100) NOT NULL,
    EMAIL VARCHAR(100) UNIQUE NOT NULL,
    SENHA VARCHAR(100) NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserir um usuário administrador padrão
INSERT INTO USUARIO (NOME, EMAIL, SENHA) VALUES ('Administrador', 'admin@lanchonete.com', 'admin123');

-- Comentário sobre a estrutura:
-- IDUSUARIO: Chave primária auto incrementável
-- NOME: Nome completo do usuário (até 100 caracteres)
-- EMAIL: Email único do usuário (até 100 caracteres)
-- SENHA: Senha do usuário (em um sistema real deveria ser criptografada)
-- CREATED_AT: Data de criação do registro
