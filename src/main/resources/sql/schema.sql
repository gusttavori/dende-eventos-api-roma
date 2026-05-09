-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
                                        id INT PRIMARY KEY,
                                        nome VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo CHAR(1) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    status_usuario BOOLEAN DEFAULT TRUE,
    tipo_usuario VARCHAR(20) NOT NULL,
    cnpj VARCHAR(18),
    razao_social VARCHAR(200),
    nome_fantasia VARCHAR(200),
    data_abertura DATE
    );

-- Tabela de Eventos
CREATE TABLE IF NOT EXISTS eventos (
                                       id INT PRIMARY KEY,
                                       organizador_id INT NOT NULL,
                                       nome VARCHAR(200) NOT NULL,
    pagina_web VARCHAR(500),
    descricao TEXT,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    modalidade VARCHAR(20) NOT NULL,
    preco_unitario_ingresso DECIMAL(10,2) NOT NULL,
    taxa_cancelamento_ingresso DECIMAL(5,2) DEFAULT 0.0,
    capacidade_maxima INT NOT NULL,
    local_evento VARCHAR(300),
    ativo BOOLEAN DEFAULT FALSE,
    evento_principal_id INT
    );

-- Tabela de Ingressos
CREATE TABLE IF NOT EXISTS ingressos (
                                         id INT PRIMARY KEY,
                                         usuario_id INT NOT NULL,
                                         evento_id INT NOT NULL,
                                         status_ingresso VARCHAR(20) DEFAULT 'ATIVO',
    valor_pago DECIMAL(10,2) NOT NULL,
    data_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Tabela de sequências
CREATE TABLE IF NOT EXISTS id_generator (
                                            sequence_name VARCHAR(50) PRIMARY KEY,
    next_id INT NOT NULL DEFAULT 1
    );

INSERT INTO id_generator (sequence_name, next_id) VALUES
                                                      ('usuario', 1),
                                                      ('evento', 1),
                                                      ('ingresso', 1);